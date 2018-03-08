package it.infn.mw.iam.test.oauth;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(properties = {"iam.access_token.include_authn_info=true"})
public class OidTokenEnhancerTests extends EndpointsTestUtils {

  private static final String CLIENT_CREDENTIALS_CLIENT_ID = "token-lookup-client";
  private static final String CLIENT_CREDENTIALS_CLIENT_SECRET = "secret";

  private static final String CLIENT_ID = "password-grant";
  private static final String CLIENT_SECRET = "secret";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";

  @Autowired
  private WebApplicationContext context;

  @Value("${iam.access_token.include_authn_info}")
  private Boolean includeAuthnInfo;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).alwaysDo(print())
        .build();
    assertTrue(includeAuthnInfo);
  }

  private String getAccessTokenForUser(String scopes) throws Exception {

    return new AccessTokenGetter().grantType("password").clientId(CLIENT_ID)
        .clientSecret(CLIENT_SECRET).username(USERNAME).password(PASSWORD).scope(scopes)
        .getAccessTokenValue();
  }

  private String getAccessTokenForClient(String scopes) throws Exception {

    return new AccessTokenGetter().grantType("client_credentials")
        .clientId(CLIENT_CREDENTIALS_CLIENT_ID).clientSecret(CLIENT_CREDENTIALS_CLIENT_SECRET)
        .scope(scopes).getAccessTokenValue();
  }

  @Test
  public void testEnhancedEmailOk() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid email"));
    System.out.println(token.getJWTClaimsSet());
    assertNotNull(token.getJWTClaimsSet().getClaim("email"));
  }

  @Test
  public void testClientCredentialsAccessTokenIsNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForClient("openid profile email"));
    System.out.println(token.getJWTClaimsSet());
    assertNull(token.getJWTClaimsSet().getClaim("email"));
    assertNull(token.getJWTClaimsSet().getClaim("preferred_username"));
    assertNull(token.getJWTClaimsSet().getClaim("organisation_name"));
    assertNull(token.getJWTClaimsSet().getClaim("groups"));
  }

  @Test
  public void testEnhancedProfileClaimsOk() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile"));
    System.out.println(token.getJWTClaimsSet());
    assertNotNull(token.getJWTClaimsSet().getClaim("preferred_username"));
    assertNotNull(token.getJWTClaimsSet().getClaim("organisation_name"));
    assertNotNull(token.getJWTClaimsSet().getClaim("groups"));
  }

  @Test
  public void testEnhancedEmailNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid"));

    System.out.println(token.getJWTClaimsSet());
    assertNull(token.getJWTClaimsSet().getClaim("email"));
  }

  @Test
  public void testEnhancedProfileClaimsNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid"));
    System.out.println(token.getJWTClaimsSet());
    assertNull(token.getJWTClaimsSet().getClaim("preferred_username"));
    assertNull(token.getJWTClaimsSet().getClaim("organisation_name"));
    assertNull(token.getJWTClaimsSet().getClaim("groups"));
  }

}
