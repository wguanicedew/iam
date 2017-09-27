package it.infn.mw.iam.test.oauth.scope;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Sets;

import it.infn.mw.iam.api.scim.converter.DefaultScimResourceLocationProvider;
import it.infn.mw.iam.api.scim.converter.ScimResourceLocationProvider;
import it.infn.mw.iam.api.scope_policy.DefaultScopePolicyConverter;
import it.infn.mw.iam.api.scope_policy.IamAccountRefDTO;
import it.infn.mw.iam.api.scope_policy.IamGroupRefDTO;
import it.infn.mw.iam.api.scope_policy.InvalidScopePolicyError;
import it.infn.mw.iam.api.scope_policy.ScopePolicyDTO;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@RunWith(MockitoJUnitRunner.class)
public class ScopePolicyConverterTests {

  private ScimResourceLocationProvider locationProvider = new DefaultScimResourceLocationProvider();

  @Mock
  private IamAccountRepository accountRepository;

  @Mock
  private IamGroupRepository groupRepository;


  private DefaultScopePolicyConverter converter;

  @Before
  public void setup() {

    converter =
        new DefaultScopePolicyConverter(locationProvider, accountRepository, groupRepository);

    when(accountRepository.findByUuid(Mockito.anyString())).thenReturn(Optional.empty());
    when(groupRepository.findByUuid(Mockito.anyString())).thenReturn(Optional.empty());

  }

  
  @Test(expected=InvalidScopePolicyError.class)
  public void testInvalidAccountIdTriggersException(){
    ScopePolicyDTO policyDTO = new ScopePolicyDTO();
    
    IamAccountRefDTO accountRef = new IamAccountRefDTO();
    accountRef.setUuid(UUID.randomUUID().toString());
    
    policyDTO.setAccount(accountRef);
    policyDTO.setScopes(Sets.newHashSet("s1"));
    policyDTO.setRule("DENY");
    
    try{
      converter.toModel(policyDTO);
    } catch(InvalidScopePolicyError e){
      assertThat(e.getMessage(), startsWith("No account found"));
      throw e;
    }
  }
  
  @Test(expected=InvalidScopePolicyError.class)
  public void testInvalidGroupIdTriggersException(){
    ScopePolicyDTO policyDTO = new ScopePolicyDTO();
    
    IamGroupRefDTO groupRef = new IamGroupRefDTO();
    groupRef.setUuid(UUID.randomUUID().toString());
    
    policyDTO.setGroup(groupRef);
    policyDTO.setScopes(Sets.newHashSet("s1"));
    policyDTO.setRule("DENY");
    
    try{
      converter.toModel(policyDTO);
    } catch(InvalidScopePolicyError e){
      assertThat(e.getMessage(), startsWith("No group found"));
      throw e;
    }
  }

}
