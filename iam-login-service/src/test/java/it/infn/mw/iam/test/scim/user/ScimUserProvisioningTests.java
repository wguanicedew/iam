package it.infn.mw.iam.test.scim.user;

import static com.jayway.restassured.matcher.ResponseAwareMatcherComposer.and;
import static com.jayway.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertNull;

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
import it.infn.mw.iam.api.scim.model.ScimIndigoUser;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@Transactional
@WebIntegrationTest
public class ScimUserProvisioningTests {

  private final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime();

  private String accessToken;
  private ScimRestUtils restUtils;

  @BeforeClass
  public static void init() {

    JacksonUtils.initRestAssured();
  }

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read scim:write");
    restUtils = ScimRestUtils.getInstance(accessToken);
  }

  @Test
  public void testGetUserNotFoundResponse() {

    String randomUuid = UUID.randomUUID().toString();

    restUtils.doGet("/scim/Users/" + randomUuid, HttpStatus.NOT_FOUND)
      .body("status", equalTo("404"))
      .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"));
  }

  @Test
  public void testUpdateUserNotFoundResponse() {

    String randomUuid = UUID.randomUUID().toString();

    ScimUser user = ScimUser.builder("john_lennon")
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .id(randomUuid)
      .build();

    restUtils.doPut("/scim/Users/" + randomUuid, user, HttpStatus.NOT_FOUND)
      .body("status", equalTo("404"))
      .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExistingUserAccess() {

    // Some existing user as defined in the test db
    String userId = "80e5fb8d-b7c8-451a-89ba-346ae278a66f";

    restUtils.doGet("/scim/Users/" + userId)
      .body("id", equalTo(userId))
      .body("userName", equalTo("test"))
      .body("displayName", equalTo("test"))
      .body("active", equalTo(true))
      .body("name.formatted", equalTo("Test User"))
      .body("name.givenName", equalTo("Test"))
      .body("name.familyName", equalTo("User"))
      .body("meta.resourceType", equalTo("User"))
      .body("meta.location", equalTo("http://localhost:8080/scim/Users/" + userId))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo("test@iam.test"))
      .body("emails[0].type", equalTo("work"))
      .body("emails[0].primary", equalTo(true))
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

    ScimUser user = ScimUser.builder()
      .userName(username)
      .buildEmail("test@email.test")
      .buildName("Paul", "McCartney")
      .build();

    ScimUser createdUser = restUtils.doPost("/scim/Users/", user)
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .as(ScimUser.class);

    restUtils.doGet(createdUser.getMeta().getLocation())
      .body("id", equalTo(createdUser.getId()))
      .body("userName", equalTo(createdUser.getUserName()))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo(createdUser.getEmails().get(0).getValue()));

    restUtils.doDelete(createdUser.getMeta().getLocation());
  }

  @Test
  public void testEmptyUsernameValidationError() {

    String username = "";

    ScimUser user = ScimUser.builder(username)
      .buildEmail("test@email.test")
      .buildName("Paul", "McCartney")
      .build();

    restUtils.doPost("/scim/Users/", user, HttpStatus.BAD_REQUEST).body("detail",
        containsString("scimUser.userName : may not be empty"));
  }

  @Test
  public void testEmptyEmailValidationError() {

    ScimUser user = ScimUser.builder("paul").buildName("Paul", "McCartney").build();

    restUtils.doPost("/scim/Users/", user, HttpStatus.BAD_REQUEST).body("detail",
        containsString("scimUser.emails : may not be empty"));
  }

  @Test
  public void testInvalidEmailValidationError() {

    ScimUser user = ScimUser.builder("paul")
      .buildEmail("this_is_not_an_email")
      .buildName("Paul", "McCartney")
      .build();

    restUtils.doPost("/scim/Users/", user, HttpStatus.BAD_REQUEST).body("detail",
        containsString("scimUser.emails[0].value : not a well-formed email address"));
  }

  @Test
  public void testUserUpdateChangeUsername() {

    ScimUser user = ScimUser.builder("john_lennon")
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .build();

    ScimUser createdUser = restUtils.doPost("/scim/Users/", user).extract().as(ScimUser.class);

    ScimUser updatedUser = ScimUser.builder("j.lennon")
      .id(user.getId())
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .active(true)
      .build();

    restUtils.doPut(createdUser.getMeta().getLocation(), updatedUser)
      .body("id", equalTo(createdUser.getId()))
      .body("userName", equalTo("j.lennon"))
      .body("emails[0].value", equalTo(createdUser.getEmails().get(0).getValue()))
      .body("meta.created",
          equalTo(dateTimeFormatter.print(createdUser.getMeta().getCreated().getTime())))
      .body("active", equalTo(true));

    restUtils.doDelete(createdUser.getMeta().getLocation());

  }

  @Test
  public void testUpdateUserValidation() {

    ScimUser user = ScimUser.builder("john_lennon")
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .build();

    ScimUser createdUser = restUtils.doPost("/scim/Users/", user).extract().as(ScimUser.class);

    ScimUser updatedUser = ScimUser.builder("j.lennon").id(user.getId()).active(true).build();

    restUtils.doPut(createdUser.getMeta().getLocation(), updatedUser, HttpStatus.BAD_REQUEST)
      .body("detail", containsString("scimUser.emails : may not be empty"));

    restUtils.doDelete(createdUser.getMeta().getLocation());
  }

  @Test
  public void testNonExistentUserDeletionReturns404() {

    String randomUserLocation = "http://localhost:8080/scim/Users/" + UUID.randomUUID().toString();

    restUtils.doDelete(randomUserLocation, HttpStatus.NOT_FOUND);

  }

  @Test
  public void testUserCreationWithPassword() {
    ScimUser user = ScimUser.builder("user_with_password")
      .buildEmail("up@test.org")
      .buildName("User", "With Password")
      .password("a_password")
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user).extract().as(ScimUser.class);

    assertNull(creationResult.getPassword());

    passwordTokenGetter().scope("openid")
      .username("user_with_password")
      .password("a_password")
      .getAccessToken();

    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

  @Test
  public void testUpdateUsernameChecksValidation() {

    ScimUser lennon = ScimUser.builder("john_lennon")
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .build();

    ScimUser lennonCreationResult =
        restUtils.doPost("/scim/Users/", lennon).extract().as(ScimUser.class);

    ScimUser mccartney = ScimUser.builder("paul_mccartney")
      .buildEmail("test@email.test")
      .buildName("Paul", "McCartney")
      .build();

    ScimUser mccartneyCreationResult =
        restUtils.doPost("/scim/Users/", mccartney).extract().as(ScimUser.class);

    ScimUser lennonWantsToBeMcCartney = ScimUser.builder("paul_mccartney")
      .id(lennon.getId())
      .buildEmail("lennon@email.test")
      .buildName("John", "Lennon")
      .build();

    restUtils
      .doPut(lennonCreationResult.getMeta().getLocation(), lennonWantsToBeMcCartney,
          HttpStatus.BAD_REQUEST)
      .body("detail", equalTo("userName is already mappped to another user"));

    restUtils.doDelete(lennonCreationResult.getMeta().getLocation());
    restUtils.doDelete(mccartneyCreationResult.getMeta().getLocation());

  }

  @Test
  public void testUserCreationWithOidcAccount() {

    ScimUser user = ScimUser.builder("user_with_oidc")
      .buildEmail("test_user@test.org")
      .buildName("User", "With OIDC Account")
      .buildOidcId("urn:oidc:test:issuer", "1234")
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user)
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
          equalTo("urn:oidc:test:issuer"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].subject", equalTo("1234"))
      .extract()
      .as(ScimUser.class);

    restUtils.doGet(creationResult.getMeta().getLocation())
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
          equalTo("urn:oidc:test:issuer"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].subject", equalTo("1234"));

    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

  @Test
  public void testUserCreationWithStolenOidcAccountFailure() {

    ScimUser user = ScimUser.builder("user_with_oidc")
      .buildEmail("test_user@test.org")
      .buildName("User", "With OIDC Account")
      .buildOidcId("urn:oidc:test:issuer", "1234")
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user)
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
          equalTo("urn:oidc:test:issuer"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".oidcIds[0].subject", equalTo("1234"))
      .extract()
      .as(ScimUser.class);

    ScimUser anotherUser = ScimUser.builder("another_user_with_oidc")
      .buildEmail("another_test_user@test.org")
      .buildName("Another User", "With OIDC Account")
      .buildOidcId("urn:oidc:test:issuer", "1234")
      .active(true)
      .build();

    restUtils.doPost("/scim/Users/", anotherUser, HttpStatus.CONFLICT).body("detail",
        equalTo("OIDC id urn:oidc:test:issuer,1234 is already mapped to another user"));

    restUtils.doDelete(creationResult.getMeta().getLocation());

  }

  @Test
  public void testUserCreationWithSshKey() {

    ScimUser user = ScimUser.builder("user_with_sshkey")
      .buildEmail("test_user@test.org")
      .buildName("User", "With ssh key Account")
      .buildSshKey("Personal", TestUtils.getSshKey(), true)
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user)
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].display", equalTo("Personal"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].value", equalTo(TestUtils.getSshKey()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].fingerprint",
          equalTo(TestUtils.getSshKeySHA256Fingerprint()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].primary", equalTo(true))
      .extract()
      .as(ScimUser.class);

    restUtils.doGet(creationResult.getMeta().getLocation())
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].display", equalTo("Personal"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].value", equalTo(TestUtils.getSshKey()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].fingerprint",
          equalTo(TestUtils.getSshKeySHA256Fingerprint()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].primary", equalTo(true));

    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

  @Test
  public void testUserCreationWithSshKeyValueOnly() {

    ScimUser user = ScimUser.builder("user_with_sshkey")
      .buildEmail("test_user@test.org")
      .buildName("User", "With ssh key Account")
      .indigoUserInfo(ScimIndigoUser.builder()
        .addSshKey(ScimSshKey.builder().value(TestUtils.getSshKey()).build())
        .build())
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user)
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys", hasSize(equalTo(1)))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].display",
          equalTo(user.getUserName() + "'s personal ssh key"))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].value", equalTo(TestUtils.getSshKey()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].fingerprint",
          equalTo(TestUtils.getSshKeySHA256Fingerprint()))
      .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].primary", equalTo(true))
      .extract()
      .as(ScimUser.class);

    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

  @Test
  public void testUserCreationWithStolenSshKeyFailure() {

    ScimUser user = ScimUser.builder("user_with_sshkey")
      .buildEmail("test_user@test.org")
      .buildName("User", "With ssh key")
      .buildSshKey("Personal", TestUtils.getSshKey(), true)
      .active(true)
      .build();

    ScimUser creationResult =
        restUtils.doPost("/scim/Users/", user)
          .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys", hasSize(equalTo(1)))
          .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].display", equalTo("Personal"))
          .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].value",
              equalTo(TestUtils.getSshKey()))
          .body(ScimConstants.INDIGO_USER_SCHEMA + ".sshKeys[0].fingerprint",
              equalTo(TestUtils.getSshKeySHA256Fingerprint()))
          .extract()
          .as(ScimUser.class);

    ScimUser anotherUser = ScimUser.builder("another_user_with_sshkey")
      .buildEmail("another_test_user@test.org")
      .buildName("Another User", "With ssh key")
      .buildSshKey("Personal", TestUtils.getSshKey(), true)
      .active(true)
      .build();

    restUtils.doPost("/scim/Users/", anotherUser, HttpStatus.CONFLICT).body("detail",
        equalTo("ssh key " + TestUtils.getSshKeySHA256Fingerprint()
            + " is already mapped to another user"));

    restUtils.doDelete(creationResult.getMeta().getLocation());

  }

  @Test
  public void testUniqueUsernameCreationCheck() {
    ScimUser user = ScimUser.builder("user1")
      .buildEmail("user1@test.org")
      .buildName("User", "1")
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users", user).extract().as(ScimUser.class);


    restUtils.doPost("/scim/Users", user, HttpStatus.CONFLICT);
    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

}
