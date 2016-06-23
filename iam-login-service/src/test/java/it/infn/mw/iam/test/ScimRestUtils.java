package it.infn.mw.iam.test;

import static com.jayway.restassured.RestAssured.given;

import org.springframework.http.HttpStatus;

import com.jayway.restassured.response.ValidatableResponse;

public class ScimRestUtils {

  public static final String SCIM_CONTENT_TYPE = "application/scim+json";

  private final String accessToken;

  private ScimRestUtils(String accessToken) {

    this.accessToken = accessToken;
  }

  public static ScimRestUtils getInstance(String accessToken) {

    return new ScimRestUtils(accessToken);
  }

  public <B> ValidatableResponse doPost(String location, B content) {

    return doPost(location, content, HttpStatus.CREATED);
  }

  public <B> ValidatableResponse doPost(String location, B content, HttpStatus expectedError) {

    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(content)
      .log()
      .all(true)
      .when()
      .post(location)
      .then()
      .log()
      .all(true)
      .statusCode(expectedError.value())
      .contentType(SCIM_CONTENT_TYPE);
  }

  public <B> ValidatableResponse doPut(String location, B content) {

    return doPut(location, content, HttpStatus.OK);
  }

  public <B> ValidatableResponse doPut(String location, B content,
      HttpStatus expectedResponseCode) {

    /** @formatter:off */
    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(content)
      .log()
      .all(true)
      .when()
      .put(location)
      .then()
      .log()
      .all(true)
      .statusCode(expectedResponseCode.value());
    /** @formatter:on */
  }

  public void doDelete(String location, HttpStatus statusExpected) {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .when()
      .delete(location)
      .then()
      .statusCode(statusExpected.value());
    /** @formatter:on */
  }

  public void doDelete(String location) {

    doDelete(location, HttpStatus.NO_CONTENT);
  }

  public ValidatableResponse doGet(String location) {

    return doGet(location, HttpStatus.OK);
  }

  public ValidatableResponse doGet(String location, HttpStatus expectedStatus) {

    /** @formatter:off */
    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(location)
      .then()
      .statusCode(expectedStatus.value());
    /** @formatter:on */
  }

  public <T> ValidatableResponse doPatch(String location, T content, HttpStatus expectedStatus) {

    /** @formatter:off */
    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(content)
      .log()
      .all(true)
      .when()
      .patch(location)
      .then()
      .log()
      .all(true)
      .statusCode(expectedStatus.value());
    /** @formatter:on */
  }

  public <T> ValidatableResponse doPatch(String location, T content) {

    return doPatch(location, content, HttpStatus.NO_CONTENT);
  }
}
