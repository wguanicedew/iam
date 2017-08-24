package it.infn.mw.iam.test.repository;

import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamScope;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class IamScopePolicyRepositoryTests {

  @Autowired
  EntityManager em;
  
  @Autowired
  IamScopePolicyRepository repo;
  
  
  @Test
  public void testDefaultPermitPolicyExists(){
    
    List<IamScopePolicy> defaultPolicies = repo.findDefaultPolicies();
    
    assertThat(defaultPolicies, not(empty()));
    assertThat(defaultPolicies, hasSize(1));
    
    IamScopePolicy defaultPolicy = defaultPolicies.get(0);
    
    assertThat(defaultPolicy.getGroup(), nullValue());
    assertThat(defaultPolicy.getScopes(), empty());
    assertThat(defaultPolicy.getRule(), equalTo(IamScopePolicy.Rule.PERMIT)); 
  }
  
  @Test
  public void testAddDefaultDenyPolicy(){
    
    IamScope scimWriteScope = new IamScope();
    scimWriteScope.setScope("scim:write");
    
    Date now = new Date();
    
    em.persist(scimWriteScope);
    
    
    IamScopePolicy denyPolicy = new IamScopePolicy();
    denyPolicy.setScopes(newHashSet(scimWriteScope));
    denyPolicy.setRule(Rule.DENY);
    denyPolicy.setCreationTime(now);
    denyPolicy.setLastUpdateTime(now);
    
    em.persist(denyPolicy);
    em.flush();
    
    List<IamScopePolicy> defaultPolicies = repo.findDefaultPolicies();
    
    assertThat(defaultPolicies, not(empty()));
    assertThat(defaultPolicies, hasSize(2));
    
    List<IamScopePolicy> scimWritePolicies = repo.findByScope("scim:write");
    assertThat(scimWritePolicies, not(empty()));
    assertThat(scimWritePolicies, hasSize(1));
    
  }

}
