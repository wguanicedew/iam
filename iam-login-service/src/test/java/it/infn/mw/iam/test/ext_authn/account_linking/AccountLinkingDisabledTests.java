package it.infn.mw.iam.test.ext_authn.account_linking;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@TestPropertySource(properties = {"accountLinking.disable=true"})
@Transactional
public class AccountLinkingDisabledTests {

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }


  @Test
  @WithMockUser(username = "test")
  public void accountLinkingDisabledWorkAsExpected() throws Throwable {
    
    mvc.perform(post("/iam/account-linking/OIDC").with(csrf().asHeader()))
      .andExpect(status().isForbidden());
    
    mvc.perform(get("/iam/account-linking/OIDC/done").with(csrf().asHeader()))
    .andExpect(status().isForbidden());

    mvc.perform(delete("/iam/account-linking/OIDC")
        .param("sub",  "sub").param("iss", "iss").with(csrf().asHeader()))
      .andExpect(status().isForbidden());
    
    mvc.perform(post("/iam/account-linking/SAML").with(csrf().asHeader()))
    .andExpect(status().isForbidden());
  
  mvc.perform(get("/iam/account-linking/SAML/done").with(csrf().asHeader()))
  .andExpect(status().isForbidden());

  mvc.perform(delete("/iam/account-linking/SAML")
      .param("sub",  "sub").param("iss", "iss")
      .param("attr",  "attr")
      .with(csrf().asHeader()))
    .andExpect(status().isForbidden());

  }



}
