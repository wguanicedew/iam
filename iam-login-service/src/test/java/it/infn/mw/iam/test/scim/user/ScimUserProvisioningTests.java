package it.infn.mw.iam.test.scim.user;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.matcher.ResponseAwareMatcherComposer.and;
import static com.jayway.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

import java.util.UUID;

import javax.transaction.Transactional;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.test.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@Transactional
@WebIntegrationTest
public class ScimUserProvisioningTests {

  private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();

  private String accessToken;

  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read scim:write");
  }

  private void deleteUser(String userLocation) {

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).when()
        .delete(userLocation).then().statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).when()
        .get(userLocation).then().statusCode(HttpStatus.NOT_FOUND.value());

  }

  @Test
  public void testGetUserNotFoundResponse() {

    String randomUuid = UUID.randomUUID().toString();

    given().port(8080).auth().preemptive().oauth2(accessToken).when()
        .get("/scim/Users/" + randomUuid).then().log().body(true)
        .statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo("404"))
        .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"))
        .contentType(SCIM_CONTENT_TYPE);

  }

  @Test
  public void testUpdateUserNotFoundResponse() {

    ScimUser user = ScimUser.builder("john_lennon").buildEmail("lennon@email.test")
        .buildName("John", "Lennon").build();

    String randomUuid = UUID.randomUUID().toString();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(user).when().put("/scim/Users/" + randomUuid).then().log().body(true)
        .statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo("404"))
        .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"))
        .contentType(SCIM_CONTENT_TYPE);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExistingUserAccess() {

    // Some existing user as defined in the test db
    String userId = "80e5fb8d-b7c8-451a-89ba-346ae278a66f";

    given().port(8080).auth().preemptive().oauth2(accessToken).when().get("/scim/Users/" + userId)
        .then().contentType(SCIM_CONTENT_TYPE).log().body(true).statusCode(HttpStatus.OK.value())
        .body("id", equalTo(userId)).body("userName", equalTo("test"))
        .body("displayName", equalTo("test")).body("active", equalTo(true))
        .body("name.formatted", equalTo("Test User")).body("name.givenName", equalTo("Test"))
        .body("name.familyName", equalTo("User")).body("meta.resourceType", equalTo("User"))
        .body("meta.location", equalTo("http://localhost:8080/scim/Users/" + userId))
        .body("emails", hasSize(equalTo(1))).body("emails[0].value", equalTo("test@iam.test"))
        .body("emails[0].type", equalTo("work")).body("emails[0].primary", equalTo(true))
        .body("groups", hasSize(equalTo(2)))
        .body("groups[0].$ref",
            and(startsWith("http://localhost:8080/scim/Groups/"), endsWithPath("groups[0].value")))
        .body("groups[1].$ref",
            and(startsWith("http://localhost:8080/scim/Groups/"), endsWithPath("groups[1].value")))
        .body("schemas", contains(ScimUser.USER_SCHEMA, ScimConstants.INDIGO_USER_SCHEMA))
        .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds", hasSize(greaterThan(0)))
        .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
            equalTo("https://accounts.google.com"))
        .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].subject",
            equalTo("105440632287425289613"));

  }

  @Test
  public void testUserCreationAccessDeletion() {

    String username = "paul_mccartney";

    ScimUser user = ScimUser.builder().userName(username).buildEmail("test@email.test")
        .buildName("Paul", "McCartney").build();

    ScimUser createdUser = given().port(8080).auth().preemptive().oauth2(accessToken)
        .contentType(SCIM_CONTENT_TYPE).body(user).log().all(true).when().post("/scim/Users/")
        .then().log().all(true).statusCode(HttpStatus.CREATED.value()).extract().as(ScimUser.class);

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).when()
        .get(createdUser.getMeta().getLocation()).then().log().all(true)
        .statusCode(HttpStatus.OK.value()).body("id", equalTo(createdUser.getId()))
        .body("userName", equalTo(createdUser.getUserName())).body("emails", hasSize(equalTo(1)))
        .body("emails[0].value", equalTo(createdUser.getEmails().get(0).getValue()));

    deleteUser(createdUser.getMeta().getLocation());
  }

  @Test
  public void testEmptyUsernameValidationError() {

    String username = "";

    ScimUser user = ScimUser.builder(username).buildEmail("test@email.test")
        .buildName("Paul", "McCartney").build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(user).log().all(true).when().post("/scim/Users/").then()
        .body("detail", containsString("scimUser.userName : may not be empty")).log().all(true)
        .statusCode(HttpStatus.BAD_REQUEST.value());

  }

  @Test
  public void testEmptyEmailValidationError() {

    ScimUser user = ScimUser.builder("paul").buildName("Paul", "McCartney").build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(user).log().all(true).when().post("/scim/Users/").then()
        .body("detail", containsString("scimUser.emails : may not be empty")).log().all(true)
        .statusCode(HttpStatus.BAD_REQUEST.value());

  }

  @Test
  public void testInvalidEmailValidationError() {

    ScimUser user = ScimUser.builder("paul").buildEmail("this_is_not_an_email")
        .buildName("Paul", "McCartney").build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(user).log().all(true).when().post("/scim/Users/").then()
        .body("detail",
            containsString("scimUser.emails[0].value : not a well-formed email address"))
        .log().all(true).statusCode(HttpStatus.BAD_REQUEST.value());

  }

  @Test
  public void testUserUpdateChangeUsername() {

    ScimUser user = ScimUser.builder("john_lennon").buildEmail("lennon@email.test")
        .buildName("John", "Lennon").build();

    ScimUser createdUser = given().port(8080).auth().preemptive().oauth2(accessToken)
        .contentType(SCIM_CONTENT_TYPE).body(user).log().all(true).when().post("/scim/Users/")
        .then().log().all(true).statusCode(HttpStatus.CREATED.value()).extract().as(ScimUser.class);

    ScimUser updatedUser = ScimUser.builder("j.lennon").id(user.getId())
        .buildEmail("lennon@email.test").buildName("John", "Lennon").active(true).build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(updatedUser).log().all(true).when().put(createdUser.getMeta().getLocation()).then()
        .log().all(true).statusCode(HttpStatus.OK.value()).body("id", equalTo(createdUser.getId()))
        .body("userName", equalTo("j.lennon"))
        .body("emails[0].value", equalTo(createdUser.getEmails().get(0).getValue()))
        .body("meta.created",
            equalTo(dateTimeFormatter.print(createdUser.getMeta().getCreated().getTime())))
        .body("active", equalTo(true));

    deleteUser(createdUser.getMeta().getLocation());

  }

  @Test
  public void testUpdateUserValidation() {

    ScimUser user = ScimUser.builder("john_lennon").buildEmail("lennon@email.test")
        .buildName("John", "Lennon").build();

    ScimUser createdUser = given().port(8080).auth().preemptive().oauth2(accessToken)
        .contentType(SCIM_CONTENT_TYPE).body(user).log().all(true).when().post("/scim/Users/")
        .then().log().all(true).statusCode(HttpStatus.CREATED.value()).extract().as(ScimUser.class);

    ScimUser updatedUser = ScimUser.builder("j.lennon").id(user.getId()).active(true).build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(updatedUser).log().all(true).when().put(createdUser.getMeta().getLocation()).then()
        .log().all(true).statusCode(HttpStatus.BAD_REQUEST.value())
        .body("detail", containsString("scimUser.emails : may not be empty"));

    deleteUser(createdUser.getMeta().getLocation());
  }

  @Test
  public void testNonExistentUserDeletionReturns404() {

    String randomUserLocation = "http://localhost:8080/scim/Users/" + UUID.randomUUID().toString();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).when()
        .delete(randomUserLocation).then().statusCode(HttpStatus.NOT_FOUND.value());

  }

  @Test
  public void testUpdateUsernameChecksValidation() {

    ScimUser lennon = ScimUser.builder("john_lennon").buildEmail("lennon@email.test")
        .buildName("John", "Lennon").build();

    ScimUser lennonCreationResult = given().port(8080).auth().preemptive().oauth2(accessToken)
        .contentType(SCIM_CONTENT_TYPE).body(lennon).log().all(true).when().post("/scim/Users/")
        .then().log().all(true).statusCode(HttpStatus.CREATED.value()).extract().as(ScimUser.class);

    ScimUser mccartney = ScimUser.builder("paul_mccartney").buildEmail("test@email.test")
        .buildName("Paul", "McCartney").build();

    ScimUser mccartneyCreationResult = given().port(8080).auth().preemptive().oauth2(accessToken)
        .contentType(SCIM_CONTENT_TYPE).body(mccartney).log().all(true).when().post("/scim/Users/")
        .then().log().all(true).statusCode(HttpStatus.CREATED.value()).extract().as(ScimUser.class);

    ScimUser lennonWantsToBeMcCartney = ScimUser.builder("paul_mccartney").id(lennon.getId())
        .buildEmail("lennon@email.test").buildName("John", "Lennon").build();

    given().port(8080).auth().preemptive().oauth2(accessToken).contentType(SCIM_CONTENT_TYPE)
        .body(lennonWantsToBeMcCartney).log().all(true).when()
        .put(lennonCreationResult.getMeta().getLocation()).then().log().all(true)
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("detail", equalTo("userName is already mappped to another user"));

    deleteUser(lennonCreationResult.getMeta().getLocation());
    deleteUser(mccartneyCreationResult.getMeta().getLocation());

  }

}
