package it.infn.mw.iam.test.oauth;



import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
@Transactional
public class IntrospectionEndpointTests {

  @Value("${iam.organisation.name}")
  String organisationName;
  
  @Test
  public void testIntrospectionEndpointRetursBasicUserInformation() {
    String accessToken =
        passwordTokenGetter().username("test").password("password").getAccessToken();

 // @formatter:off
    given()
      .port(8080)
      .auth()
        .preemptive()
          .basic("password-grant", "secret")
      .formParam("token", accessToken)
    .when()
      .post("/introspect")
    .then()
      .log().body(true)
      .statusCode(HttpStatus.OK.value())
      .body("active", equalTo(true))
      .body("groups", hasSize(Matchers.equalTo(2)))
      .body("groups", containsInAnyOrder("Production", "Analysis"))
      .body("preferred_username", equalTo("test"))
      .body("organisation_name", equalTo(organisationName))
      .body("email", equalTo("test@iam.test"));
    // @formatter:on
  }

  @Test
  public void testNoGroupsReturnedWithoutProfileScope() {
    String accessToken = passwordTokenGetter().username("test")
      .password("password")
      .scope("openid")
      .getAccessToken();

 // @formatter:off
    given()
      .port(8080)
      .auth()
        .preemptive()
          .basic("password-grant", "secret")
      .formParam("token", accessToken)
    .when()
      .post("/introspect")
    .then()
      .log().body(true)
      .statusCode(HttpStatus.OK.value())
      .body("active", equalTo(true))
      .body("groups", nullValue())
      .body("preferred_username", nullValue())
      .body("organisation_name", nullValue())
      .body("email", nullValue());
    // @formatter:on
  }

  @Test
  public void testEmailReturnedWithEmailScope() {
    String accessToken = passwordTokenGetter().username("test")
      .password("password")
      .scope("openid email")
      .getAccessToken();

 // @formatter:off
    given()
      .port(8080)
      .auth()
        .preemptive()
          .basic("password-grant", "secret")
      .formParam("token", accessToken)
    .when()
      .post("/introspect")
    .then()
      .log().body(true)
      .statusCode(HttpStatus.OK.value())
      .body("active", equalTo(true))
      .body("groups", nullValue())
      .body("preferred_username", nullValue())
      .body("organisation_name", nullValue())
      .body("email", equalTo("test@iam.test"));
    // @formatter:on
  }

}
