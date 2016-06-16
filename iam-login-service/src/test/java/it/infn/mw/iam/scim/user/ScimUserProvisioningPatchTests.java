package it.infn.mw.iam.scim.user;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimIndigoUser;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType;
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimUserProvisioningPatchTests {

  public static final String SCIM_CONTENT_TYPE = "application/scim+json";

  private String accessToken;

  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret",
      "scim:read scim:write");
  }

  private ScimUser addTestUser(String userName, String email, String firstName,
    String LastName) {

    ScimUser lennon = ScimUser.builder(userName)
      .buildEmail(email)
      .buildName(firstName, LastName)
      .build();

    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(lennon)
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
  }

  private ScimUserPatchRequest getPatchRequest(ScimPatchOperationType op,
    ScimUser updates) {

    ScimPatchOperation<ScimUser> userOp = (new ScimPatchOperation.Builder<ScimUser>())
      .op(op)
      .value(updates)
      .build();

    return ScimUserPatchRequest.builder()
      .addOperation(userOp)
      .build();
  }
  
  private void doPatch(ScimUserPatchRequest req, String location, HttpStatus expectedStatus) {
    
    given().port(8080)
    .auth()
    .preemptive()
    .oauth2(accessToken)
    .contentType(SCIM_CONTENT_TYPE)
    .body(req)
    .log()
    .all(true)
    .when()
    .patch(location)
    .then()
    .log()
    .all(true)
    .statusCode(expectedStatus.value());
  }

  private void deleteUser(String userLocation) {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .delete(userLocation)
      .then()
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(userLocation)
      .then()
      .statusCode(HttpStatus.NOT_FOUND.value());

  }

  @Test
  public void patchUserInfo() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");

    ScimAddress address = ScimAddress.builder()
      .country("IT")
      .formatted("viale Berti Pichat 6/2\nBologna IT")
      .locality("Bologna")
      .postalCode("40121")
      .region("Emilia Romagna")
      .streetAddress("viale Berti Pichat")
      .build();

    ScimName name = ScimName.builder()
      .givenName("John jr.")
      .familyName("Lennon II")
      .middleName("Francis")
      .build();

    /*
     * Update: - email - username - active - name - address
     */
    ScimUser lennon_update = ScimUser.builder("john_lennon_jr")
      .buildEmail("john_lennon_jr@email.com")
      .active(!lennon.getActive())
      .name(name)
      .addAddress(address)
      .build();

    ScimUserPatchRequest req = getPatchRequest(ScimPatchOperationType.add,
      lennon_update);

    doPatch(req, lennon.getMeta().getLocation(), HttpStatus.NO_CONTENT);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(lennon.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("userName", equalTo(lennon_update.getUserName()))
      .body("displayName", equalTo(lennon_update.getUserName()))
      .body("name.givenName", equalTo(name.getGivenName()))
      .body("name.familyName", equalTo(name.getFamilyName()))
      .body("name.middleName", equalTo(name.getMiddleName()))
      .body("name.formatted", equalTo(name.getFormatted()))
      .body("active", equalTo(lennon_update.getActive()))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo(lennon_update.getEmails()
        .get(0)
        .getValue()))
      .body("addresses", hasSize(equalTo(1)))
      .body("addresses[0].formatted", equalTo(address.getFormatted()))
      .body("addresses[0].streetAddress", equalTo(address.getStreetAddress()))
      .body("addresses[0].locality", equalTo(address.getLocality()))
      .body("addresses[0].region", equalTo(address.getRegion()))
      .body("addresses[0].postalCode", equalTo(address.getPostalCode()))
      .body("addresses[0].country", equalTo(address.getCountry()));

    deleteUser(lennon.getMeta()
      .getLocation());
  }

  @Test
  public void patchAddRessignAndRemoveOidcId() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    ScimUser lincoln = addTestUser("abraham_lincoln", "lincoln@email.test",
      "Abraham", "Lincoln");

    ScimIndigoUser indigoUser = ScimIndigoUser.builder()
      .addOidcid(ScimOidcId.builder()
        .issuer("test_issuer")
        .subject("test_subject")
        .build())
      .build();

    ScimUser updateOidcId = ScimUser.builder()
      .indigoUserInfo(indigoUser)
      .build();

    ScimUserPatchRequest req = getPatchRequest(ScimPatchOperationType.add,
      updateOidcId);

    doPatch(req, lennon.getMeta().getLocation(), HttpStatus.NO_CONTENT);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(lennon.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(lennon.getId()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.oidcIds[0].issuer",
        equalTo(indigoUser.getOidcIds()
          .get(0)
          .getIssuer()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.oidcIds[0].subject",
        equalTo(indigoUser.getOidcIds()
          .get(0)
          .getSubject()));

    /* lincoln tryes to add the oidc account: */
    doPatch(req, lincoln.getMeta().getLocation(), HttpStatus.CONFLICT);
    
    /* Remove oidc account */
    req = getPatchRequest(ScimPatchOperationType.remove, updateOidcId);

    doPatch(req, lennon.getMeta().getLocation(), HttpStatus.NO_CONTENT);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(lennon.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(lennon.getId()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser", equalTo(null));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteUser(lincoln.getMeta()
      .getLocation());
  }

  @Test
  public void patchRemoveNotExistingOidcId() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .indigoUserInfo(ScimIndigoUser.builder()
        .addOidcid(ScimOidcId.builder()
          .issuer("test_issuer")
          .subject("test_subject")
          .build())
        .build())
      .build();

    ScimUserPatchRequest req = getPatchRequest(ScimPatchOperationType.remove,
      lennon_update);

    doPatch(req, lennon.getMeta().getLocation(), HttpStatus.NOT_FOUND);

    deleteUser(lennon.getMeta()
      .getLocation());
  }

}
