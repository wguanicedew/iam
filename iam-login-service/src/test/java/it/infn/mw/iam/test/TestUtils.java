package it.infn.mw.iam.test;

import static com.jayway.restassured.RestAssured.given;

import org.springframework.http.HttpStatus;

public class TestUtils {

  public static String CLIENT_CRED_GRANT_CLIENT_ID = "client-cred";
  public static String CLIENT_CRED_GRANT_CLIENT_SECRET = "secret";
  public static String PASSWORD_GRANT_CLIENT_ID = "password-grant";
  public static String PASSWORD_GRANT_CLIENT_SECRET = "secret";

  private TestUtils() {}

  public static String getAccessToken(String clientId, String clientSecret, String scope) {
    return clientCredentialsTokenGetter(clientId, clientSecret).scope(scope).getAccessToken();
  }

  public static AccessTokenGetter clientCredentialsTokenGetter(String clientId,
      String clientSecret) {
    return new AccessTokenGetter(clientId, clientSecret).grantType("client_credentials");
  }
  
  public static AccessTokenGetter clientCredentialsTokenGetter(){
    return new AccessTokenGetter(CLIENT_CRED_GRANT_CLIENT_ID, CLIENT_CRED_GRANT_CLIENT_SECRET)
        .grantType("client_credentials");
  }


  public static AccessTokenGetter passwordTokenGetter() {
    return new AccessTokenGetter(PASSWORD_GRANT_CLIENT_ID, PASSWORD_GRANT_CLIENT_SECRET)
        .grantType("password");
  }

  public static AccessTokenGetter passwordTokenGetter(String clientId, String clientSecret) {
    return new AccessTokenGetter(clientId, clientSecret).grantType("password");
  }

  public static class AccessTokenGetter {
    private String clientId;
    private String clientSecret;
    private String scope;
    private String grantType;
    private String username;
    private String password;
    private int port = 8080;

    public AccessTokenGetter(String clientId, String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
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


    public String getAccessToken() {

      switch (grantType) {
        case "client_credentials":
          return given().port(port).param("grant_type", grantType).param("client_id", clientId)
              .param("client_secret", clientSecret).param("scope", scope).when().post("/token")
              .then().log().all(true).statusCode(HttpStatus.OK.value()).extract()
              .path("access_token");

        case "password":
          return given().port(port).param("grant_type", grantType).param("client_id", clientId)
              .param("client_secret", clientSecret).param("scope", scope)
              .param("username", username).param("password", password).when().post("/token").then()
              .log().all(true).statusCode(HttpStatus.OK.value()).extract().path("access_token");

        default:
          throw new IllegalArgumentException("Unsupported grant type: " + grantType);
      }
    }
  }
}
