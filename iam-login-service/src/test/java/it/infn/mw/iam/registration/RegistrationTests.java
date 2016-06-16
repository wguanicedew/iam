package it.infn.mw.iam.registration;

import static com.jayway.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.core.IamRegistrationRequestStatus;
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class RegistrationTests {

  @Autowired
  private PersistentUUIDTokenGenerator generator;

  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Test
  public void testCreateRequest() {

    String uuid = createRegistrationRequest("test_create");

    Assert.notNull(uuid);
  }

  @Test
  public void testListNewRequests() {

    String accessToken = TestUtils.getAccessToken("registration-client",
      "secret", "registration:list");

    createRegistrationRequest("test_list_new");

    // @formatter:off
    given()
      .port(8080)
        .auth()
          .preemptive()
          .oauth2(accessToken)
      .param("status", IamRegistrationRequestStatus.NEW)
    .when()
      .get("/registration")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.OK.value())
      .body("size()", Matchers.greaterThanOrEqualTo(1))
    ;
    // @formatter:on
  }

  @Test
  public void testListRequestsUnauthorized() {

    // @formatter:off
    given()
      .port(8080)
      .param("status", IamRegistrationRequestStatus.NEW)
    .when()
      .get("/registration")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.UNAUTHORIZED.value())
    ;
    // @formatter:on
  }

  @Test
  public void testConfirmRequest() {

    createRegistrationRequest("test_confirm");

    String token = generator.getLastToken();
    Assert.notNull(token);

    confirmRegistrationRequest(token);
  }

  @Test
  public void testConfirmRequestFailureWithWrongToken() {

    createRegistrationRequest("test_confirm_fail");

    String badToken = "abcdefghilmnopqrstuvz";

    // @formatter:off
    given()
      .port(8080)
      .pathParam("token", badToken)
    .when()
      .post("/registration/confirm/{token}")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.NOT_FOUND.value());
    // @formatter:on
  }

  @Test
  public void testApproveRequest() {

    String accessToken = TestUtils.getAccessToken("registration-client",
      "secret", "registration:update");

    // create new request
    String uuid = createRegistrationRequest("test_approve");
    Assert.notNull(uuid);

    String token = generator.getLastToken();
    Assert.notNull(token);

    // confirm
    confirmRegistrationRequest(token);

    // approve it
    // @formatter:off
    given()
      .port(8080)
        .auth()
          .preemptive()
          .oauth2(accessToken)
      .param("decision", IamRegistrationRequestStatus.APPROVED.name())
      .pathParam("uuid", uuid)
    .when()
      .post("/registration/{uuid}")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.OK.value())
      .body("status", Matchers.equalTo(IamRegistrationRequestStatus.APPROVED.name()))
      .body("uuid", Matchers.equalTo(uuid))
    ;
    // @formatter:on
  }

  @Test
  public void testRejectRequest() {

    String accessToken = TestUtils.getAccessToken("registration-client",
      "secret", "registration:update");

    // create new request
    String uuid = createRegistrationRequest("test_reject");
    Assert.notNull(uuid);

    String token = generator.getLastToken();
    Assert.notNull(token);

    // confirm
    confirmRegistrationRequest(token);

    // @formatter:off
    // reject it
    given()
      .port(8080)
        .auth()
          .preemptive()
          .oauth2(accessToken)
      .param("decision", IamRegistrationRequestStatus.REJECTED.name())
      .pathParam("uuid", uuid)
    .when()
      .post("/registration/{uuid}")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.OK.value())
      .body("status", Matchers.equalTo(IamRegistrationRequestStatus.REJECTED.name()))
      .body("uuid", Matchers.equalTo(uuid))
    ;
    // @formatter:on
  }

  private String createRegistrationRequest(final String username) {

    String email = username + "@example.org";

    ScimUser user = new ScimUser.Builder(username).buildEmail(email)
      .buildName("Test", "User")
      .build();

    // @formatter:off
    String uuid = given()
      .port(8080)
      .contentType(ScimConstants.SCIM_CONTENT_TYPE)
      .body(user)
      .log()
        .all(true)
    .when()
      .post("/registration")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.OK.value())
      .extract()
      .path("uuid");
      ;
    // @formatter:on

    return uuid;
  }

  private void confirmRegistrationRequest(final String token) {

    // @formatter:off
    given()
      .port(8080)
      .pathParam("token", token)
    .when()
      .post("/registration/confirm/{token}")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.OK.value())
      .body("status", Matchers.equalTo(IamRegistrationRequestStatus.CONFIRMED.name()));
    // @formatter:on
  }

}
