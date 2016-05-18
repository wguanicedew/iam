package it.infn.mw.iam.scim;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.matcher.ResponseAwareMatcherComposer.and;
import static com.jayway.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimUserTests {

  public static final String SCIM_CONTENT_TYPE = "application/scim+json";

  @Test
  public void testNotFoundResponse() {

    String randomUuid = UUID.randomUUID()
      .toString();

    given().port(8080)
      .when()
      .get("/scim/Users/" + randomUuid)
      .then()
      .log()
      .body(true)
      .statusCode(HttpStatus.NOT_FOUND.value())
      .body("status", equalTo("404"))
      .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"))
      .contentType(SCIM_CONTENT_TYPE);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExistingUserAccess() {

    // Some existing user as defined in the test db
    String userId = "80e5fb8d-b7c8-451a-89ba-346ae278a66f";

    given().port(8080)
      .when()
      .get("/scim/Users/" + userId)
      .then()
      .contentType(SCIM_CONTENT_TYPE)
      .log()
      .body(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(userId))
      .body("userName", equalTo("test"))
      .body("displayName", equalTo("test"))
      .body("active", equalTo(true))
      .body("name.formatted", equalTo("Test User"))
      .body("name.givenName", equalTo("Test"))
      .body("name.familyName", equalTo("User"))
      .body("meta.resourceType", equalTo("User"))
      .body("meta.location",
        equalTo("http://localhost:8080/scim/Users/" + userId))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo("test@iam.test"))
      .body("emails[0].type", equalTo("work"))
      .body("emails[0].primary", equalTo(true))
      .body("groups", hasSize(equalTo(2)))
      .body("groups[0].$ref",
        and(startsWith("http://localhost:8080/scim/Groups/"),
          endsWithPath("groups[0].value")))
      .body("groups[1].$ref",
        and(startsWith("http://localhost:8080/scim/Groups/"),
          endsWithPath("groups[1].value")))
      .body("schemas",
        contains(ScimUser.USER_SCHEMA, ScimConstants.INDIGO_USER_SCHEMA))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds",
        hasSize(greaterThan(0)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
        equalTo("https://accounts.google.com"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].subject",
        equalTo("105440632287425289613"));

  }

  @Test
  public void testUserCreation() {

    String username = "paul_mccartney";

    ScimUser.Builder builder = new ScimUser.Builder(username);
    builder.buildEmail("test@email.test");
    builder.buildName("Paul", "McCartney");

    ScimUser user = builder.build();

    ScimUser createdUser = given().port(8080)
      .contentType(SCIM_CONTENT_TYPE)
      .body(user)
      .log()
      .all(true)
      .when()
      .post("/scim/Users/")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .as(ScimUser.class);

    given().port(8080)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(createdUser.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(createdUser.getId()))
      .body("userName", equalTo(createdUser.getUserName()))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo(createdUser.getEmails()
        .get(0)
        .getValue()));

  }

  @Test
  public void testEmptyUsernameValidationError() {

    String username = "";

    ScimUser.Builder builder = new ScimUser.Builder(username);
    builder.buildEmail("test@email.test");
    builder.buildName("Paul", "McCartney");

    ScimUser user = builder.build();
    given().port(8080)
      .contentType(SCIM_CONTENT_TYPE)
      .body(user)
      .log()
      .all(true)
      .when()
      .post("/scim/Users/")
      .then()
      .body("detail", containsString("scimUser.userName : may not be empty"))
      .log()
      .all(true)
      .statusCode(HttpStatus.BAD_REQUEST.value());

  }

  @Test
  public void testEmptyEmailValidationError() {

    String username = "paul";

    ScimUser.Builder builder = new ScimUser.Builder(username);
    builder.buildName("Paul", "McCartney");

    ScimUser user = builder.build();
    given().port(8080)
      .contentType(SCIM_CONTENT_TYPE)
      .body(user)
      .log()
      .all(true)
      .when()
      .post("/scim/Users/")
      .then()
      .body("detail", containsString("scimUser.emails : may not be empty"))
      .log()
      .all(true)
      .statusCode(HttpStatus.BAD_REQUEST.value());

  }

  @Test
  public void testInvalidEmailValidationError() {

    String username = "paul";

    ScimUser.Builder builder = new ScimUser.Builder(username);
    builder.buildEmail("this_is_not_an_email");
    builder.buildName("Paul", "McCartney");

    ScimUser user = builder.build();
    given().port(8080)
      .contentType(SCIM_CONTENT_TYPE)
      .body(user)
      .log()
      .all(true)
      .when()
      .post("/scim/Users/")
      .then()
      .body("detail", containsString(
        "scimUser.emails[0].value : not a well-formed email address"))
      .log()
      .all(true)
      .statusCode(HttpStatus.BAD_REQUEST.value());

  }

}
