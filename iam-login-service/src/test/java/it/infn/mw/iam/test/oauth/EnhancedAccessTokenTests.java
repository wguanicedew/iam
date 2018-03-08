package it.infn.mw.iam.test.oauth;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.Lists;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@WebAppConfiguration
@Transactional
@TestPropertySource(properties = {"iam.access_token.include_authn_info=true"})
public class EnhancedAccessTokenTests extends EndpointsTestUtils {

  private static final String CLIENT_CREDENTIALS_CLIENT_ID = "token-lookup-client";
  private static final String CLIENT_CREDENTIALS_CLIENT_SECRET = "secret";

  private static final String CLIENT_ID = "password-grant";
  private static final String CLIENT_SECRET = "secret";
  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";
  private static final String EMAIL = "test@iam.test";
  private static final String ORGANISATION = "indigo-dc";
  private static final List<String> GROUPS = Lists.newArrayList("Production", "Analysis");

  @Before
  public void setup() throws Exception {
    buildMockMvc();
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
    String email = (String) token.getJWTClaimsSet().getClaim("email");
    assertThat(email, is(notNullValue()));
    assertThat(email, is(EMAIL));
  }

  @Test
  public void testClientCredentialsAccessTokenIsNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForClient("openid profile email"));
    assertThat(token.getJWTClaimsSet().getClaim("email"), is(nullValue()));
    assertThat(token.getJWTClaimsSet().getClaim("preferred_username"), is(nullValue()));
    assertThat(token.getJWTClaimsSet().getClaim("organisation_name"), is(nullValue()));
    assertThat(token.getJWTClaimsSet().getClaim("groups"), is(nullValue()));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testEnhancedProfileClaimsOk() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid profile"));
    String preferredUsername = (String) token.getJWTClaimsSet().getClaim("preferred_username");
    assertThat(preferredUsername, is(notNullValue()));
    assertThat(preferredUsername, is(USERNAME));
    String organisationName = (String) token.getJWTClaimsSet().getClaim("organisation_name");
    assertThat(organisationName, is(notNullValue()));
    assertThat(organisationName, is(ORGANISATION));
    List<String> groups = (List<String>) token.getJWTClaimsSet().getClaim("groups");
    assertThat(groups, is(notNullValue()));
    assertThat(groups.size(), is(2));
    assertThat(GROUPS.contains(groups.get(0)), is(true));
    assertThat(GROUPS.contains(groups.get(1)), is(true));
  }

  @Test
  public void testEnhancedEmailNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid"));
    assertThat(token.getJWTClaimsSet().getClaim("email"), is(nullValue()));
  }

  @Test
  public void testEnhancedProfileClaimsNotEnhanced() throws Exception {

    JWT token = JWTParser.parse(getAccessTokenForUser("openid"));
    assertThat(token.getJWTClaimsSet().getClaim("preferred_username"), is(nullValue()));
    assertThat(token.getJWTClaimsSet().getClaim("organisation_name"), is(nullValue()));
    assertThat(token.getJWTClaimsSet().getClaim("groups"), is(nullValue()));
  }

}
