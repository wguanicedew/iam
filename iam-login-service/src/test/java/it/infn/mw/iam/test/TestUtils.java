package it.infn.mw.iam.test;

import static com.jayway.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.mapper.factory.Jackson2ObjectMapperFactory;

import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimUser;

public class TestUtils {

  public static String CLIENT_CRED_GRANT_CLIENT_ID = "client-cred";
  public static String CLIENT_CRED_GRANT_CLIENT_SECRET = "secret";
  public static String PASSWORD_GRANT_CLIENT_ID = "password-grant";
  public static String PASSWORD_GRANT_CLIENT_SECRET = "secret";

  private TestUtils() {}

  public static ObjectMapper createJacksonObjectMapper() {

    FilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);

    ObjectMapper mapper = new ObjectMapper();
    mapper.setFilterProvider(filters);
    return mapper;

  }

  public static Jackson2ObjectMapperFactory getJacksonObjectMapperFactory() {

    return new Jackson2ObjectMapperFactory() {

      @Override
      public ObjectMapper create(@SuppressWarnings("rawtypes") Class cls, String charset) {

        return createJacksonObjectMapper();
      }
    };
  }

  public static void initRestAssured() {

    RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
        new ObjectMapperConfig().jackson2ObjectMapperFactory(getJacksonObjectMapperFactory()));

  }

  public static ScimMemberRef getMemberRef(ScimUser user) {

    return ScimMemberRef.builder()
      .display(user.getDisplayName())
      .ref(user.getMeta().getLocation())
      .value(user.getId())
      .build();
  }

  public static List<ScimMemberRef> buildScimMemberRefList(List<ScimUser> users) {

    List<ScimMemberRef> membersRefs = new ArrayList<ScimMemberRef>();
    for (ScimUser u : users) {
      membersRefs.add(getMemberRef(u));
    }
    return membersRefs;
  }
  
  public static String getX509TestCertificate() {

    return "MIIEWDCCA0CgAwIBAgIDAII4MA0GCSqGSIb3DQEBCwUAMC4xCzAJBgNVBAYTAklU"
        + "MQ0wCwYDVQQKEwRJTkZOMRAwDgYDVQQDEwdJTkZOIENBMB4XDTE1MDUxODEzNTQx"
        + "NFoXDTE2MDUxNzEzNTQxNFowZDELMAkGA1UEBhMCSVQxDTALBgNVBAoTBElORk4x"
        + "HTAbBgNVBAsTFFBlcnNvbmFsIENlcnRpZmljYXRlMQ0wCwYDVQQHEwRDTkFGMRgw"
        + "FgYDVQQDEw9FbnJpY28gVmlhbmVsbG8wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw"
        + "ggEKAoIBAQDf74gCX/5D7HAKlI9u+vMy4R8uYvtZp60L401zOuDHc0sKPCq2sU8N"
        + "IB8cNOC+69h+hPqbU8gcleXZ0T3KOy3NPrU7CFaOxzsCVAoDcLeKFlCMu4X1OK0V"
        + "NPq7+fgJ1cVdsJ4StHl3oTtQPCoU6NNly8HJIufVjat2IgjNHdMHINs5IcxpTmE5"
        + "OGae3reOfRBtqBr8UvyiTwHEEll6JpdbKjzjrcHBoOdFZTiwR18fO+B8MZLOjXSk"
        + "OEG5p5K8y4UOkHQeqooKgW0tn7dvCxQfuu5TGYUmK6pwjcxzcnSE9U4abFh5/oD1"
        + "PqjoCGtlvnl9nGrhAFD+qa5zq6SrgWsNAgMBAAGjggFHMIIBQzAMBgNVHRMBAf8E"
        + "AjAAMA4GA1UdDwEB/wQEAwIEsDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUH"
        + "AwQwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3NlY3VyaXR5LmZpLmluZm4uaXQv"
        + "Q0EvSU5GTkNBX2NybC5kZXIwJQYDVR0gBB4wHDAMBgorBgEEAdEjCgEHMAwGCiqG"
        + "SIb3TAUCAgEwHQYDVR0OBBYEFIQEiwCbKssJqSBNMziZtu54ZQRCMFYGA1UdIwRP"
        + "ME2AFNFi87N3csgu+/J5Gm83TiefE9UgoTKkMDAuMQswCQYDVQQGEwJJVDENMAsG"
        + "A1UEChMESU5GTjEQMA4GA1UEAxMHSU5GTiBDQYIBADAnBgNVHREEIDAegRxlbnJp"
        + "Y28udmlhbmVsbG9AY25hZi5pbmZuLml0MA0GCSqGSIb3DQEBCwUAA4IBAQBfhv9P"
        + "4bYo7lVRYjHrxreKVaEyujzPZFowZPYMz0e/lPcdqh9TIoDBbhy7/PXiTVqQEniZ"
        + "fU1Nso4rqBj8Qy609Y60PEFHhfLnjhvd/d+pXu6F1QTzUMwA2k7z5M+ykh7L46/z"
        + "1vwvcdvCgtWZ+FedvLuKh7miTCfxEIRLcpRPggbC856BSKet7jPdkMxkUwbFa34Z"
        + "qOuDQ6MvcrFA/lLgqN1c1OoE9tnf/uyOjVYq8hyXqOAhi2heE1e+s4o3/PQsaP5x"
        + "LetVho/J33BExHo+hCMt1rN89DO5qU7FFijLlbmOZROacpjkPNn2V4wkd5WeX2dm" 
        + "b6UoBRqPsAiQL0mY";
  }
  
  public static String getAccessToken(String clientId, String clientSecret, String scope) {
    return clientCredentialsTokenGetter(clientId, clientSecret).scope(scope).getAccessToken();
  }

  public static AccessTokenGetter clientCredentialsTokenGetter(String clientId,
      String clientSecret) {
    return new AccessTokenGetter(clientId, clientSecret).grantType("client_credentials");
  }

  public static AccessTokenGetter clientCredentialsTokenGetter() {
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
          return given().port(port)
            .param("grant_type", grantType)
            .param("client_id", clientId)
            .param("client_secret", clientSecret)
            .param("scope", scope)
            .when()
            .post("/token")
            .then()
            .log()
            .all(true)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("access_token");

        case "password":
          return given().port(port)
            .param("grant_type", grantType)
            .param("client_id", clientId)
            .param("client_secret", clientSecret)
            .param("scope", scope)
            .param("username", username)
            .param("password", password)
            .when()
            .post("/token")
            .then()
            .log()
            .all(true)
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("access_token");

        default:
          throw new IllegalArgumentException("Unsupported grant type: " + grantType);
      }
    }
  }
  
  public static String getSshKey() {
    
    return "AAAAB3NzaC1yc2EAAAABIwAAAQEAxL6nllg/rMURT2QTy4MGj0gxYQ6sxcqCde5orLBs4rjIogo9bL7+HFLt6FHpCQbZ0CXakoL2M7PmXbFdwlD4Yw4ye4VxEaW3J1eNzRMWMGNTaAlcGiQqDuS/SsxI6SOlp/kfXQprDn2MnED1jIQHQq5pm25wKpKYeUBAaC6hvA4OlE39YpMCsVPEM3BhkR7F51I/60+5jV5P/g0arCnZKYJOnLmNpYc86ry8yydQMvD5HFBjRR8GRfvTU/0UcVtNsa1PzHTD7+lTA7iwDHX4cfe+4o38C850zU9yUMV+SnlLMJhwBiCxaaqeU0SdBbG+nCL47drSlSvv85+baXftSw==";
  }
  
  public static String getSshKeyMD5Fingerprint() {
    
    return "265af5c556421a4e9432f65e48b37d91";
  }
  
  public static String getSshKeyFormattedMD5Fingerprint() {
    
    return "26:5a:f5:c5:56:42:1a:4e:94:32:f6:5e:48:b3:7d:91";
  }
  
  public static String getSshKeySHA256Fingerprint() {
    
    return "dowJH1al1DJII+i7DYux1BGQkx3P+XVpaz3TIX5zt5Y=";
  }
}
