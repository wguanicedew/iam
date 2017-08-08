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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class IntrospectionEndpointAuthenticationTests extends EndpointsTestUtils {

  private static final String ENDPOINT = "/introspect";

  @Autowired
  private WebApplicationContext context;

  private String accessToken;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();

    accessToken = getPasswordAccessToken("openid profile offline_access");
  }


  @Test
  public void testTokenIntrospectionEndpointBasicAuthentication() throws Exception {
    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .with(httpBasic("password-grant", "secret"))
        .param("token", accessToken))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.active", equalTo(true)));
    // @formatter:on
  }

  @Test
  public void testTokenIntrospectionEndpointFormAuthentication() throws Exception {
    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .param("token", accessToken)
        .param("client_id", "client-cred")
        .param("client_secret", "secret"))
      .andExpect(status().isUnauthorized());
    // @formatter:on
  }

  @Test
  public void testTokenIntrospectionEndpointNoAuthenticationFailure() throws Exception {
    // @formatter:off
    mvc.perform(post(ENDPOINT)
        .param("token", accessToken))
      .andExpect(status().isUnauthorized());
   // @formatter:on
  }
}
