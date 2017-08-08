package it.infn.mw.iam.test.scim.me;

import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.core.CoreControllerTestSupport;
import it.infn.mw.iam.test.util.WithMockOAuthUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class, CoreControllerTestSupport.class})
@WebAppConfiguration
@Transactional
public class ScimMeEndpointTests {

  private final static String ME_ENDPOINT = "/scim/Me";

  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }

  @Test
  @WithMockOAuthUser(clientId = "password-grant", user = "test", authorities = {"ROLE_USER"},
      scopes = {"openid", "profile"})
  public void meEndpointUserInfo() throws Exception {
    //@formatter:off
    mvc.perform(get(ME_ENDPOINT)
        .contentType(SCIM_CONTENT_TYPE))
      .andExpect(status().isOk());
    //@formatter:on
  }

  @Test
  @WithMockOAuthUser(clientId = "registration-client", scopes = {"scim:read"})
  public void meEndpointFailsForClientWithoutUser() throws Exception {

    mvc.perform(get(ME_ENDPOINT).contentType(SCIM_CONTENT_TYPE))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status", equalTo("400")))
      .andExpect(jsonPath("$.detail", equalTo("No user linked to the current OAuth token")));
  }
}
