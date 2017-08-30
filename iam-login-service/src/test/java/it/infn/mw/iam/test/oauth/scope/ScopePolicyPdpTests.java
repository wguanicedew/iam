package it.infn.mw.iam.test.oauth.scope;


import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Sets;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.core.oauth.scope.ScopePolicyPDP;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamScope;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;
import it.infn.mw.iam.test.repository.ScopePolicyTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class ScopePolicyPdpTests extends ScopePolicyTestUtils {

  @Autowired
  IamScopePolicyRepository policyScopeRepo;

  @Autowired
  IamAccountRepository accountRepo;
  
  @Autowired
  ScopePolicyPDP pdp;
  
  
  IamAccount findTestAccount() {
    return accountRepo.findByUsername("test")
      .orElseThrow(() -> new AssertionError("Expected test account not found!"));
  }

  @Test
  public void testBasicDefaultPolicyDecision() {

    IamAccount testAccount = findTestAccount();
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:read"), testAccount);

    assertThat(filteredScopes, hasSize(3));
    assertThat(filteredScopes, hasItems("openid", "profile", "scim:read"));

  }
  
  @Test
  public void testAccountPolicyIsEnforced() {
    IamAccount testAccount = findTestAccount();
    
    IamScopePolicy up = initDenyScopePolicy();
    up.setAccount(testAccount);
    up.getScopes().add(SCIM_WRITE_SCOPE);
    
    policyScopeRepo.save(up);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:write"), testAccount);
    assertThat(filteredScopes, hasSize(2));
    assertThat(filteredScopes, hasItems("openid", "profile"));
  }
  
  @Test
  public void testAccountPolicyIsCompletelyEnforced() {
    IamAccount testAccount = findTestAccount();
    
    IamScopePolicy up = initDenyScopePolicy();
    up.setAccount(testAccount);
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.getScopes().add(OPENID_SCOPE);
    up.getScopes().add(PROFILE_SCOPE);
    
    policyScopeRepo.save(up);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet(OPENID, PROFILE, SCIM_WRITE), testAccount);
    assertThat(filteredScopes, hasSize(0));
    
  }
  
  @Test
  public void testGroupPolicyIsEnforced() {
    IamAccount testAccount = findTestAccount();
    
    Iterator<IamGroup> groupsIter =  testAccount.getGroups().iterator();
    
    IamGroup firstGroup = groupsIter.next();
    
    
    IamScopePolicy up = initDenyScopePolicy();
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.setGroup(firstGroup);
    
    policyScopeRepo.save(up);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:write"), testAccount);
    assertThat(filteredScopes, hasSize(2));
    assertThat(filteredScopes, hasItems("openid", "profile"));
  }
  
  @Test
  public void testChainedOverrideIsEnforced() {
    IamAccount testAccount = findTestAccount();
    
    Iterator<IamGroup> groupsIter =  testAccount.getGroups().iterator();
    
    IamGroup firstGroup = groupsIter.next();
    
    IamScopePolicy gp = initPermitScopePolicy();
    gp.setGroup(firstGroup);
    gp.setScopes(Sets.newHashSet(new IamScope("openid"), new IamScope("profile")));
    
    policyScopeRepo.save(gp);
    
    IamScopePolicy ap = initPermitScopePolicy();
    ap.setAccount(testAccount);
    ap.getScopes().add(new IamScope(SCIM_WRITE));
    
    policyScopeRepo.save(ap);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:write"), testAccount);
    
    assertThat(filteredScopes, hasSize(3));
    assertThat(filteredScopes, hasItems("openid", "profile", "scim:write"));
  }

  @Test
  public void testConflictingGroupPolicyDenyOverrides() {
  IamAccount testAccount = findTestAccount();
    
    Iterator<IamGroup> groupsIter =  testAccount.getGroups().iterator();
    
    IamGroup firstGroup = groupsIter.next();
    IamGroup secondGroup = groupsIter.next();
    
    IamScopePolicy up = initDenyScopePolicy();
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.setGroup(firstGroup);
    up.setDescription(firstGroup.getName());
    policyScopeRepo.save(up);
    
    up = initPermitScopePolicy();
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.setGroup(secondGroup);
    up.setDescription(secondGroup.getName());
    policyScopeRepo.save(up);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:write"), testAccount);
    assertThat(filteredScopes, hasSize(2));
    assertThat(filteredScopes, hasItems("openid", "profile"));
  }
  
  @Test
  public void testConflictingGroupPolicyDenyOverrides2() {
  IamAccount testAccount = findTestAccount();
    
    Iterator<IamGroup> groupsIter =  testAccount.getGroups().iterator();
    
    IamGroup firstGroup = groupsIter.next();
    IamGroup secondGroup = groupsIter.next();
    
    IamScopePolicy up = initPermitScopePolicy();
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.setGroup(firstGroup);
    up.setDescription(firstGroup.getName());
    policyScopeRepo.save(up);
    
    up = initDenyScopePolicy();
    up.getScopes().add(SCIM_WRITE_SCOPE);
    up.setGroup(secondGroup);
    up.setDescription(secondGroup.getName());
    policyScopeRepo.save(up);
    
    Set<String> filteredScopes =
        pdp.filterScopes(Sets.newHashSet("openid", "profile", "scim:write"), testAccount);
    assertThat(filteredScopes, hasSize(2));
    assertThat(filteredScopes, hasItems("openid", "profile"));
  }
}
