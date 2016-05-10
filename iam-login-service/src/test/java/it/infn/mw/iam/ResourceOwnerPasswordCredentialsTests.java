package it.infn.mw.iam;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ResourceOwnerPasswordCredentialsTests {

  @Test
  public void testDiscoveryEndpoint() {

    // @formatter:off
    given()
      .port(8080)
    .when()
      .get("/.well-known/openid-configuration")
      .then()
      .body("issuer", equalTo("http://localhost:8080/"));
    // @formatter:on
  }

  @Test
  public void testResourceOwnerPasswordCredentialsFlow() {

    String clientId = "password-grant";
    String clientSecret = "secret";

    // @formatter:off
    given()
       .auth()
       .preemptive().basic(clientId, clientSecret)
      .port(8080)
      .param("grant_type", "password")
      .param("username", "test_user")
      .param("password", "password")
      .param("scope", "openid profile")
    .expect()
      .log()
        .body(true)
      .statusCode(200) 
    .when()
        .post("/token")
        .then().body("scope", equalTo("openid profile"));
    // @formatter:on

  }
  
  @Test
  public void testInvalidResourceOwnerPasswordCredentials() {

    String clientId = "password-grant";
    String clientSecret = "secret";

    // @formatter:off
    given()
       .auth()
       .preemptive().basic(clientId, clientSecret)
      .port(8080)
      .param("grant_type", "password")
      .param("username", "test_user")
      .param("password", "wrong_password")
      .param("scope", "openid profile")
    .expect()
      .log()
        .body(true)
      .statusCode(400) 
    .when()
        .post("/token")
        .then()
          .body("error", equalTo("invalid_grant"))
            .and()
          .body("error_description", equalTo("Bad credentials"));
    // @formatter:on

  }
  
  @Test
  public void testResourceOwnerPasswordCredentialsInvalidClientCredentials() {

    String clientId = "password-grant";
    String clientSecret = "socret";

    // @formatter:off
    given()
       .auth()
       .preemptive().basic(clientId, clientSecret)
      .port(8080)
      .param("grant_type", "password")
      .param("username", "test_user")
      .param("password", "password")
      .param("scope", "openid profile")
    .expect()
      .log()
        .body(true)
      .statusCode(401) 
    .when()
        .post("/token")
        .then()
          .body("error", equalTo("Unauthorized"))
            .and()
          .body("message", equalTo("Bad credentials"));
    // @formatter:on

  }
  
  @Test
  public void testResourceOwnerPasswordCredentialsUnknownClient() {

    String clientId = "unknown";
    String clientSecret = "socret";

    // @formatter:off
    given()
       .auth()
       .preemptive().basic(clientId, clientSecret)
      .port(8080)
      .param("grant_type", "password")
      .param("username", "test_user")
      .param("password", "password")
      .param("scope", "openid profile")
    .expect()
      .log()
        .body(true)
      .statusCode(401) 
    .when()
        .post("/token")
        .then()
          .body("error", equalTo("Unauthorized"))
            .and()
          .body("message", equalTo("Client with id unknown was not found"));
    // @formatter:on

  }

}
