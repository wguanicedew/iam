package it.infn.mw.iam.test.oauth;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;

import it.infn.mw.iam.IamLoginService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebAppConfiguration
@Transactional
public class TokenExchangeTests extends EndpointsTestUtils {

  private static final String GRANT_TYPE = "urn:ietf:params:oauth:grant-type:token-exchange";
  private static final String TOKEN_TYPE = "urn:ietf:params:oauth:token-type:jwt";

  private static final String USERNAME = "test";
  private static final String PASSWORD = "password";
  private static final String TOKEN_ENDPOINT = "/token";

  @Autowired
  private WebApplicationContext context;

  @Autowired
  private ObjectMapper mapper;

  @Before
  public void setup() throws Exception {
    mvc = MockMvcBuilders.webAppContextSetup(context)
      .apply(springSecurity())
      .alwaysDo(print())
      .build();
  }


  @Test
  public void testImpersonationFlowWithAudience() throws Exception {

    String clientId = "token-exchange-subject";
    String clientSecret = "secret";

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String audClientId = "tasks-app";

    String accessToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid profile")
      .getAccessTokenValue();

    // @formatter:off
    String response = mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("audience", audClientId)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "read-tasks"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo("read-tasks")))
      .andExpect(jsonPath("$.issued_token_type", equalTo(TOKEN_TYPE)))
      .andExpect(jsonPath("$.token_type", equalTo("Bearer")))
      .andExpect(jsonPath("$.access_token").exists())
      .andExpect(jsonPath("$.access_token", notNullValue()))
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    DefaultOAuth2AccessToken responseToken =
        mapper.readValue(response, DefaultOAuth2AccessToken.class);
    String actorAccessToken = responseToken.getValue();

    // Check audience is encoded in JWT access token
    try {
      JWT jwtAccessToken = JWTParser.parse(actorAccessToken);
      JWTClaimsSet claims = jwtAccessToken.getJWTClaimsSet();

      assertThat(claims.getAudience(), contains("tasks-app"));
      assertThat(claims.getAudience(), hasSize(1));

    } catch (ParseException e) {
      fail(e.getMessage());
    }

    // Check audience is also returned by the introspection endpoint
    // @formatter:off
    // Introspect token
    mvc.perform(post("/introspect")
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("token", actorAccessToken))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.aud", equalTo("tasks-app")))
      .andExpect(jsonPath("$.active", equalTo(true)))
      .andExpect(jsonPath("$.scope", equalTo("read-tasks")))
      .andExpect(jsonPath("$.user_id", equalTo("test")))
      .andExpect(jsonPath("$.client_id", equalTo(actorClientId)));
    // @formatter:on

    // @formatter:off
    // get user info on token, this fails as we did not 
    // request to openid scope
    mvc.perform(post("/userinfo")
        .header("Authorization", "Bearer "+actorAccessToken))
      .andExpect(status().isForbidden());
    // @formatter:on
  }

  @Test
  public void testImpersonationFlowWithoutAudience() throws Exception {

    String clientId = "token-exchange-subject";
    String clientSecret = "secret";

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String accessToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid profile")
      .getAccessTokenValue();

    // @formatter:off
    String response = mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "read-tasks openid profile"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo("read-tasks openid profile")))
      .andExpect(jsonPath("$.issued_token_type", equalTo(TOKEN_TYPE)))
      .andExpect(jsonPath("$.token_type", equalTo("Bearer")))
      .andExpect(jsonPath("$.access_token").exists())
      .andExpect(jsonPath("$.access_token", notNullValue()))
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    DefaultOAuth2AccessToken responseToken =
        mapper.readValue(response, DefaultOAuth2AccessToken.class);
    String actorAccessToken = responseToken.getValue();

    // Check audience is NOT encoded in JWT access token
    try {
      JWT jwtAccessToken = JWTParser.parse(actorAccessToken);
      JWTClaimsSet claims = jwtAccessToken.getJWTClaimsSet();
      
      assertThat(claims.getAudience(), empty());

    } catch (ParseException e) {
      fail(e.getMessage());
    }

    // Introspect token
    // @formatter:off
    mvc.perform(post("/introspect")
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("token", actorAccessToken))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.aud").doesNotExist())
      .andExpect(jsonPath("$.active", equalTo(true)))
      .andExpect(jsonPath("$.scope", allOf(containsString("read-tasks"), containsString("openid"), containsString("profile"))))
      .andExpect(jsonPath("$.user_id", equalTo("test")))
      .andExpect(jsonPath("$.client_id", equalTo(actorClientId)));
    // @formatter:on

    // get user info on token, this fails as we did not
    // request to openid scope
    // @formatter:off
    mvc.perform(post("/userinfo")
        .header("Authorization", "Bearer " + actorAccessToken))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.sub", equalTo("80e5fb8d-b7c8-451a-89ba-346ae278a66f")));
    // @formatter:on
  }

  @Test
  public void testUnauthorizedClient() throws Exception {

    String clientId = "client-cred";
    String clientSecret = "secret";

    String audClientId = "tasks-app";

    String accessToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid profile")
      .getAccessTokenValue();

    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(clientId, clientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("audience", audClientId)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "read-tasks"))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", equalTo("invalid_client")))
      .andExpect(jsonPath("$.error_description", containsString("Unauthorized grant type")));
    // @formatter:on
  }

  @Test
  public void testTokenExchangeWithRefreshToken() throws Exception {

    String clientId = "token-exchange-subject";
    String clientSecret = "secret";

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String audClientId = "client";

    String accessToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid profile offline_access")
      .getAccessTokenValue();

    // @formatter:off
    String response = mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("audience", audClientId)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "openid offline_access read-tasks"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.scope", equalTo("read-tasks openid offline_access")))
      .andExpect(jsonPath("$.issued_token_type", equalTo(TOKEN_TYPE)))
      .andExpect(jsonPath("$.token_type", equalTo("Bearer")))
      .andExpect(jsonPath("$.access_token").exists())
      .andExpect(jsonPath("$.access_token", notNullValue()))
      .andReturn()
      .getResponse()
      .getContentAsString();
    // @formatter:on

    DefaultOAuth2AccessToken responseToken =
        mapper.readValue(response, DefaultOAuth2AccessToken.class);
    String refreshToken = responseToken.getRefreshToken().getValue();

    // use refresh token
    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", "refresh_token")
        .param("refresh_token", refreshToken)
        .param("client_id", actorClientId)
        .param("client_secret", actorClientSecret))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.access_token", notNullValue()));
    // @formatter:on
  }

  @Test
  public void testRequestRefreshTokenWithoutOfflineAccessScope() throws Exception {

    String clientId = "token-exchange-subject";
    String clientSecret = "secret";

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String audClientId = "client";

    String accessToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid")
      .getAccessTokenValue();

    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("audience", audClientId)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "openid offline_access"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error", equalTo("invalid_scope")));
    // @formatter:on
  }

  @Test
  public void testDelegationFlow() throws Exception {

    String clientId = "token-exchange-subject";
    String clientSecret = "secret";

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String audClientId = "client";

    String subjectToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid")
      .getAccessTokenValue();

    String actorToken = new AccessTokenGetter().grantType("password")
      .clientId(clientId)
      .clientSecret(clientSecret)
      .username(USERNAME)
      .password(PASSWORD)
      .scope("openid")
      .getAccessTokenValue();

    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("audience", audClientId)
        .param("subject_token", subjectToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("actor_token", actorToken)
        .param("actor_token_type", TOKEN_TYPE)
        .param("want_composite", "true")
        .param("scope", "read-tasks"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error", equalTo("invalid_request")))
      .andExpect(jsonPath("$.error_description", containsString("not supported")));
    // @formatter:on
  }

  @Test
  public void testWithInvalidSubjectToken() throws Exception {

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    String accessToken = "abcdefghilmnopqrstuvz0123456789";

    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "read-tasks"))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.error", equalTo("invalid_token")));
    // @formatter:on
  }

  @Test
  public void testTokenExchangeFailureForClientCredentialsClient() throws Exception {

    String accessToken = new AccessTokenGetter().grantType("client_credentials")
      .clientId("client-cred")
      .clientSecret("secret")
      .scope("read-tasks write-tasks")
      .getAccessTokenValue();

    String actorClientId = "token-exchange-actor";
    String actorClientSecret = "secret";

    // @formatter:off
    mvc.perform(post(TOKEN_ENDPOINT)
        .with(httpBasic(actorClientId, actorClientSecret))
        .param("grant_type", GRANT_TYPE)
        .param("subject_token", accessToken)
        .param("subject_token_type", TOKEN_TYPE)
        .param("scope", "read-tasks"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.error", equalTo("invalid_request")))
      .andExpect(jsonPath("$.error_description", containsString("No user identity linked to subject token.")));
    // @formatter:on
  }
}
