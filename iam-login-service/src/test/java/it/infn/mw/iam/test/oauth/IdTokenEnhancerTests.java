package it.infn.mw.iam.test.oauth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
public class IdTokenEnhancerTests {

  private static final String CLIENT_ID = "password-grant";
  private static final String CLIENT_SECRET = "secret";
  private static final String GRANT_TYPE = "password";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  private MockMvc mvc;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(print())
        .build();
  }

  private String getIdToken(String scopes) throws Exception {

    // @formatter:off
    String response = mvc.perform(post("/token")
        .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
        .param("grant_type", GRANT_TYPE)
        .param("username", USERNAME)
        .param("password", PASSWORD)
        .param("scope", scopes))
      .andExpect(status().isOk())
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    DefaultOAuth2AccessToken tokenResponse =
        mapper.readValue(response, DefaultOAuth2AccessToken.class);

    return tokenResponse.getAdditionalInformation().get("id_token").toString();
  }

  @Test
  public void testEnhancedEmailOk() throws Exception {

    JWT token = JWTParser.parse(getIdToken("openid email"));
    System.out.println(token.getJWTClaimsSet());
    assertNotNull(token.getJWTClaimsSet().getClaim("email"));
  }

  @Test
  public void testEnhancedProfileClaimsOk() throws Exception {

    JWT token = JWTParser.parse(getIdToken("openid profile"));
    System.out.println(token.getJWTClaimsSet());
    assertNotNull(token.getJWTClaimsSet().getClaim("preferred_username"));
    assertNotNull(token.getJWTClaimsSet().getClaim("organisation_name"));
    assertNotNull(token.getJWTClaimsSet().getClaim("groups"));
  }

  @Test
  public void testEnhancedEmailNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getIdToken("openid"));

    System.out.println(token.getJWTClaimsSet());
    assertNull(token.getJWTClaimsSet().getClaim("email"));
  }

  @Test
  public void testEnhancedProfileClaimsNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getIdToken("openid"));
    System.out.println(token.getJWTClaimsSet());
    assertNull(token.getJWTClaimsSet().getClaim("preferred_username"));
    assertNull(token.getJWTClaimsSet().getClaim("organisation_name"));
    assertNull(token.getJWTClaimsSet().getClaim("groups"));
  }
}
