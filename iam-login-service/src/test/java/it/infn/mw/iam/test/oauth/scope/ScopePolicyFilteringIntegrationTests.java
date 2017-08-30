package it.infn.mw.iam.test.oauth.scope;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Sets;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamScopePolicyRepository;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.repository.ScopePolicyTestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class ScopePolicyFilteringIntegrationTests extends ScopePolicyTestUtils{

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private IamAccountRepository accountRepo;

  @Autowired
  private IamScopePolicyRepository scopePolicyRepo;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .alwaysDo(print())
      .apply(springSecurity())
      .build();
  }
  
  IamAccount findTestAccount() {
    return accountRepo.findByUsername("test")
      .orElseThrow(() -> new AssertionError("Expected test account not found!"));
  }
  
  @Test
  public void testScopeFilteringByAccountWorks() throws Exception {
    
    IamAccount testAccount = findTestAccount();
    
    IamScopePolicy up = initDenyScopePolicy();
    up.setAccount(testAccount);
    up.setRule(Rule.DENY);
    up.setScopes(Sets.newHashSet(SCIM_READ_SCOPE));
    
    scopePolicyRepo.save(up);
    
    String clientId = "password-grant";
    String clientSecret = "secret";

    mvc.perform(post("/token")
        .with(httpBasic(clientId, clientSecret))
        .param("grant_type", "password")
        .param("username", "test")
        .param("password", "password")
        .param("scope", "openid profile scim:read"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo("openid profile")));
  }
  
  @Test
  public void testScopeFilteringDenyAllScopesWorks() throws Exception {
    
    IamAccount testAccount = findTestAccount();
    
    IamScopePolicy up = initDenyScopePolicy();
    up.setAccount(testAccount);
    up.setRule(Rule.DENY);
    
    scopePolicyRepo.save(up);
    
    String clientId = "password-grant";
    String clientSecret = "secret";

    mvc.perform(post("/token")
        .with(httpBasic(clientId, clientSecret))
        .param("grant_type", "password")
        .param("username", "test")
        .param("password", "password")
        .param("scope", "openid profile scim:read"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope").doesNotExist())
      .andExpect(jsonPath("$.id_token").exists());
  }

}
