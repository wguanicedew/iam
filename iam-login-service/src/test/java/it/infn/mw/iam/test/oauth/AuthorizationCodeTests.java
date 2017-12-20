package it.infn.mw.iam.test.oauth;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.util.WithAnonymousUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebAppConfiguration
@WithAnonymousUser
@Transactional
public class AuthorizationCodeTests {

  public static final String TEST_CLIENT_ID = "client";
  public static final String TEST_CLIENT_SECRET = "secret";
  public static final String TEST_CLIENT_REDIRECT_URI =
      "https://iam.local.io/iam-test-client/openid_connect_login";

  public static final String LOGIN_URL = "http://localhost/login";
  public static final String AUTHORIZE_URL = "http://localhost/authorize";

  public static final String RESPONSE_TYPE_CODE = "code";
  
  public static final String SCOPE = "openid profile";

  public static final String TEST_USER_ID = "test";
  public static final String TEST_USER_PASSWORD = "password";

  @Autowired
  WebApplicationContext context;

  @Autowired
  ObjectMapper objectMapper;

  MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }


  @Test
  public void testOidcAuthorizationCodeFlow() throws Exception {

    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(AUTHORIZE_URL)
        .queryParam("response_type", RESPONSE_TYPE_CODE)
        .queryParam("client_id", TEST_CLIENT_ID)
        .queryParam("redirect_uri", TEST_CLIENT_REDIRECT_URI)
        .queryParam("scope", SCOPE)
        .queryParam("nonce", "1")
        .queryParam("state", "1").build();
    
    String authzEndpointUrl =  uriComponents.toUriString();

    MockHttpSession session = (MockHttpSession) mvc
      .perform(get(authzEndpointUrl))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOGIN_URL))
      .andReturn()
      .getRequest()
      .getSession();

    session = (MockHttpSession) mvc
      .perform(post(LOGIN_URL).param("username", TEST_USER_ID)
        .param("password", TEST_USER_PASSWORD)
        .param("submit", "Login")
        .session(session))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(uriComponents.encode().toUriString()))
      .andReturn()
      .getRequest()
      .getSession();

  }


}
