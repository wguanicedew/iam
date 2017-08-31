package it.infn.mw.iam.test.oauth;

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

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebAppConfiguration
@Transactional
public class TokenEndpointClientAuthenticationTests {

  private static final String ENDPOINT = "/token";
  private static final String GRANT_TYPE = "client_credentials";
  private static final String SCOPE = "read-tasks";

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
  public void testTokenEndpointFormClientAuthentication() throws Exception {

    String clientId = "post-client";
    String clientSecret = "secret";

    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .param("grant_type", GRANT_TYPE)
        .param("client_id", clientId)
        .param("client_secret", clientSecret)
        .param("scope", SCOPE))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo(SCOPE)));
    // @formatter:on
  }

  @Test
  public void testTokenEndpointFormClientAuthenticationInvalidCredentials() throws Exception {

    String clientId = "post-client";
    String clientSecret = "wrong-password";

    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .param("grant_type", GRANT_TYPE)
        .param("client_id", clientId)
        .param("client_secret", clientSecret)
        .param("scope", SCOPE))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", equalTo("invalid_client")))
      .andExpect(jsonPath("$.error_description", equalTo("Bad client credentials")));
    // @formatter:on
  }

  @Test
  public void testTokenEndpointFormClientAuthenticationUnknownClient() throws Exception {

    String clientId = "unknown-client";
    String clientSecret = "password";

    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .param("grant_type", GRANT_TYPE)
        .param("client_id", clientId)
        .param("client_secret", clientSecret)
        .param("scope", SCOPE))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", equalTo("invalid_client")))
      .andExpect(jsonPath("$.error_description", equalTo("Client with id unknown-client was not found")));
    // @formatter:on
  }

  @Test
  public void testTokenEndpointBasicClientAuthentication() throws Exception {

    String clientId = "post-client";
    String clientSecret = "secret";

    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .with(httpBasic(clientId, clientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("scope", SCOPE))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo(SCOPE)));
    // @formatter:on
  }
}
