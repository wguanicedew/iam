package it.infn.mw.iam.test;

import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;

import org.hamcrest.Matchers;
import org.springframework.http.HttpStatus;

import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.core.IamRegistrationRequestStatus;
import it.infn.mw.iam.registration.RegistrationRequestDto;

public class RegistrationUtils {

  public static RegistrationRequestDto createRegistrationRequest(String username) {

    String email = username + "@example.org";

    ScimUser user =
        new ScimUser.Builder(username).buildEmail(email).buildName("Test", "User").build();

    // @formatter:off
    RegistrationRequestDto reg = given()
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
            .as(RegistrationRequestDto.class)
    ;
    // @formatter:on

    return reg;
  }

  public static void deleteUser(String userId) {

    String accessToken =
        TestUtils.getAccessToken("registration-client", "secret", "scim:write scim:read");

    String location = "/scim/Users/" + userId;
    // @formatter:off
    given()
      .port(8080)
      .auth()
        .preemptive()
        .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
    .when()
      .delete(location)
    .then()
      .statusCode(HttpStatus.NO_CONTENT.value());

    given()
      .port(8080)
      .auth()
        .preemptive()
        .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
    .when()
      .get(location)
    .then()
      .statusCode(HttpStatus.NOT_FOUND.value())
    ;
    // @formatter:on
  }

  public static void confirmRegistrationRequest(String token) {

    // @formatter:off
    given()
      .port(8080)
      .pathParam("token", token)
    .when()
      .get("/registration/confirm/{token}")
    .then()
      .statusCode(HttpStatus.OK.value())
      .body("status", Matchers.equalTo(IamRegistrationRequestStatus.CONFIRMED.name()))
    ;
    // @formatter:on
  }

  public static void approveRequest(String uuid) {
    makeDecision(uuid, IamRegistrationRequestStatus.APPROVED);
  }

  public static void rejectRequest(String uuid) {
    makeDecision(uuid, IamRegistrationRequestStatus.REJECTED);
  }

  private static void makeDecision(String uuid, IamRegistrationRequestStatus decision) {
    String accessToken =
        TestUtils.getAccessToken("registration-client", "secret", "registration:write");

    // @formatter:off
    given()
      .port(8080)
      .auth()
        .preemptive()
        .oauth2(accessToken)
      .pathParam("uuid", uuid)
      .pathParam("decision", decision)
    .when()
      .post("/registration/{uuid}/{decision}")
    .then()
      .statusCode(HttpStatus.OK.value())
    ;
    // @formatter:on
  }

}
