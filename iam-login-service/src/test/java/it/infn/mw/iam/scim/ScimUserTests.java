package it.infn.mw.iam.scim;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimUser;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimUserTests {

  @Test
  public void testNotFoundResponse() {

    String randomUuid = UUID.randomUUID().toString();

    given().port(8080).expect().log().body(true)
      .statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo("404"))
      .body("detail", equalTo("No user mapped to id '" + randomUuid + "'"))
      .when().get("/scim/Users/" + randomUuid);

  }

  @Test
  public void testExistingUserAccess() {

    String userId = "80e5fb8d-b7c8-451a-89ba-346ae278a66f";

    // @format:off
    given().port(8080).expect().log().body(true)
      .statusCode(HttpStatus.OK.value()).body("id", equalTo(userId))
      .body("userName", equalTo("test")).body("displayName", equalTo("test"))
      .body("active", equalTo(true))
      .body("name.formatted", equalTo("Test User"))
      .body("name.givenName", equalTo("Test"))
      .body("name.familyName", equalTo("User"))
      .body("meta.resourceType", equalTo("User"))
      .body("meta.location",
        equalTo("http://localhost:8080/scim/Users/" + userId))
      .body("emails", hasSize(greaterThan(0)))
      .body("emails[0].value", equalTo("test@iam.test"))
      .body("emails[0].type", equalTo("work"))
      .body("emails[0].primary", equalTo(true))
      .body("schemas",
        Matchers.containsInAnyOrder(ScimUser.USER_SCHEMA,
          ScimUser.INDIGO_USER_SCHEMA))
      .body(ScimUser.INDIGO_USER_SCHEMA + ".oidcIds", hasSize(greaterThan(0)))
      .body(ScimUser.INDIGO_USER_SCHEMA + ".oidcIds[0].issuer",
        equalTo("https://accounts.google.com"))
      .body(ScimUser.INDIGO_USER_SCHEMA + ".oidcIds[0].subject",
        equalTo("105440632287425289613"))
      .when().get("/scim/Users/" + userId);
    // @format:on

  }

}
