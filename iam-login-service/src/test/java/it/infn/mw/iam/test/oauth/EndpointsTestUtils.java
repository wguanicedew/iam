package it.infn.mw.iam.test.oauth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EndpointsTestUtils {

  private static final String DEFAULT_USERNAME = "test";
  private static final String DEFAULT_PASSWORD = "password";
  private static final String DEFAULT_CLIENT_ID = "password-grant";
  private static final String DEFAULT_CLIENT_SECRET = "secret";
  private static final String DEFAULT_SCOPE = "";


  @Autowired
  ObjectMapper mapper;

  MockMvc mvc;

  protected String getPasswordAccessToken(String scope) throws Exception {
    return new AccessTokenGetter().grantType("password")
      .clientId(DEFAULT_CLIENT_ID)
      .clientSecret(DEFAULT_CLIENT_SECRET)
      .scope(scope)
      .username(DEFAULT_USERNAME)
      .password(DEFAULT_PASSWORD)
      .getAccessToken();
  }

  protected String getPasswordAccessToken() throws Exception {
    return new AccessTokenGetter().grantType("password")
      .clientId(DEFAULT_CLIENT_ID)
      .clientSecret(DEFAULT_CLIENT_SECRET)
      .scope(DEFAULT_SCOPE)
      .username(DEFAULT_USERNAME)
      .password(DEFAULT_PASSWORD)
      .getAccessToken();
  }


  public class AccessTokenGetter {
    private String clientId;
    private String clientSecret;
    private String scope;
    private String grantType;
    private String username;
    private String password;
    private String audience;

    public AccessTokenGetter clientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public AccessTokenGetter clientSecret(String clientSecret) {
      this.clientSecret = clientSecret;
      return this;
    }

    public AccessTokenGetter scope(String scope) {
      this.scope = scope;
      return this;
    }

    public AccessTokenGetter grantType(String grantType) {
      this.grantType = grantType;
      return this;
    }

    public AccessTokenGetter username(String username) {
      this.username = username;
      return this;
    }

    public AccessTokenGetter password(String password) {
      this.password = password;
      return this;
    }

    public AccessTokenGetter audience(String audience) {
      this.audience = audience;
      return this;
    }

    public String getAccessToken() throws Exception {
      MockHttpServletRequestBuilder req = post("/token").param("grant_type", grantType)
        .param("client_id", clientId)
        .param("client_secret", clientSecret)
        .param("scope", scope);

      if ("password".equals(grantType)) {
        req.param("username", username).param("password", password);
      }

      if (audience != null) {
        req.param("aud", audience);
      }

      //@formatter:off
        String response = mvc.perform(req)
          .andExpect(status().isOk())
          .andReturn()
          .getResponse()
          .getContentAsString();
        //@formatter:on

      DefaultOAuth2AccessToken accessToken =
          mapper.readValue(response, DefaultOAuth2AccessToken.class);

      return accessToken.getValue();
    }
  }

}
