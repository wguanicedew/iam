package it.infn.mw.iam.test.repository;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Sets;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;
import it.infn.mw.iam.persistence.repository.IamScopeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class IamScopePolicyRepositoryTests extends ScopePolicyTestUtils {

  @Autowired
  EntityManager em;

  @Autowired
  IamScopePolicyRepository policyRepo;

  @Autowired
  IamScopeRepository scopeRepo;

  @Autowired
  IamGroupRepository groupRepo;

  @Autowired
  IamAccountRepository accountRepo;

  @Test
  public void testDefaultPermitPolicyExists() {

    List<IamScopePolicy> defaultPolicies = policyRepo.findDefaultPolicies();

    assertThat(defaultPolicies, not(empty()));
    assertThat(defaultPolicies, hasSize(1));

    IamScopePolicy defaultPolicy = defaultPolicies.get(0);

    assertThat(defaultPolicy.getGroup(), nullValue());
    assertThat(defaultPolicy.getScopes(), empty());
    assertThat(defaultPolicy.getRule(), equalTo(IamScopePolicy.Rule.PERMIT));
  }

  @Test
  public void testCascadePersistOnScopeWorks() {

    IamScopePolicy policy = initDenyScopePolicy();
    policy.setScopes(Sets.newHashSet(SCIM_READ_SCOPE, SCIM_WRITE_SCOPE));
    policyRepo.save(policy);

    IamScopePolicy policy2 = initDenyScopePolicy();
    policy2.setScopes(Sets.newHashSet(WHATEVER_SCOPE, SCIM_READ_SCOPE)); // this policy is redundant
    policyRepo.save(policy2);

    assertThat(scopeRepo.count(), equalTo(3L));

    List<IamScopePolicy> defaultPolicies = policyRepo.findDefaultPolicies();

    assertThat(defaultPolicies, not(empty()));
    assertThat(defaultPolicies, hasSize(3));
  }

  @Test
  public void testGroupPolicyCreationWorks() {

    IamGroup analysisGroup = groupRepo.findByName("Analysis")
      .orElseThrow(() -> new AssertionError("Expected Analysis group not found"));

    IamScopePolicy policy = initDenyScopePolicy();
    policy.setGroup(analysisGroup);
    policy.setRule(Rule.PERMIT);
    policy.setScopes(Sets.newHashSet(SCIM_WRITE_SCOPE));

    policyRepo.save(policy);

    List<IamScopePolicy> policies = policyRepo.findByGroup(analysisGroup);

    assertThat(policies, hasSize(1));
    assertThat(policies.get(0).getGroup(), equalTo(analysisGroup));
    assertThat(policies.get(0).getAccount(), nullValue());
    assertThat(policies.get(0).getRule(), equalTo(Rule.PERMIT));

    assertThat(policies.get(0).getScopes(), hasSize(1));
    assertThat(policies.get(0).getScopes(), hasItem(SCIM_WRITE_SCOPE));
  }

  @Test
  public void testUserPolicyCreationWorks() {

    IamAccount testAccount = accountRepo.findByUsername("test")
      .orElseThrow(() -> new AssertionError("Expected 'test' user not found"));

    IamScopePolicy policy = initDenyScopePolicy();
    policy.setAccount(testAccount);
    policy.setRule(Rule.PERMIT);
    policy.setScopes(Sets.newHashSet(SCIM_WRITE_SCOPE));

    policyRepo.save(policy);

    List<IamScopePolicy> policies = policyRepo.findByAccount(testAccount);

    assertThat(policies, hasSize(1));
    
    assertThat(policies.get(0).getAccount(), equalTo(testAccount));
    assertThat(policies.get(0).getGroup(), nullValue());
    assertThat(policies.get(0).getRule(), equalTo(Rule.PERMIT));

    assertThat(policies.get(0).getScopes(), hasSize(1));
    assertThat(policies.get(0).getScopes(), hasItem(SCIM_WRITE_SCOPE));
  }

  @Test
  public void testUserPolicyIsRemovedWhenUserIsRemoved() {

    IamAccount testAccount = accountRepo.findByUsername("test")
      .orElseThrow(() -> new AssertionError("Expected 'test' user not found"));

    IamScopePolicy policy = initDenyScopePolicy();
    policy.setAccount(testAccount);
    policy.setRule(Rule.PERMIT);
    policy.setScopes(Sets.newHashSet(SCIM_WRITE_SCOPE));

    policyRepo.save(policy);

    List<IamScopePolicy> policies = policyRepo.findByAccount(testAccount);

    assertThat(policies, hasSize(1));
    
    accountRepo.delete(testAccount);
    
    assertThat(policyRepo.findByAccount(testAccount), hasSize(0)); 
  }

  @Test
  public void testGroupPolicyIsRemovedWhenGroupIsRemoved() {

    IamGroup analysisGroup = groupRepo.findByName("Analysis")
      .orElseThrow(() -> new AssertionError("Expected Analysis group not found"));

    IamScopePolicy policy = initDenyScopePolicy();
    policy.setGroup(analysisGroup);
    policy.setRule(Rule.PERMIT);
    policy.setScopes(Sets.newHashSet(SCIM_WRITE_SCOPE));

    policyRepo.save(policy);

    IamScopePolicy policy2 = initDenyScopePolicy();
    policy2.setGroup(analysisGroup);
    policy2.setRule(Rule.DENY);
    policy2.setScopes(Sets.newHashSet(WHATEVER_SCOPE));

    policyRepo.save(policy2);

    List<IamScopePolicy> policies = policyRepo.findByGroup(analysisGroup);
    assertThat(policies, hasSize(2));

    groupRepo.delete(analysisGroup);
    policies = policyRepo.findByGroup(analysisGroup);
    assertThat(policies, hasSize(0));

  }

}
