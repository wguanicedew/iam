package it.infn.mw.iam.test.scim.user;

import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNull;

import java.util.Base64;

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
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimUserProvisioningPatchTests {

  private String accessToken;
  private ScimRestUtils restUtils;

  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read scim:write");
    restUtils = ScimRestUtils.getInstance(accessToken);
  }

  private ScimUser addTestUser(String userName, String email, String firstName, String LastName) {

    ScimUser lennon =
        ScimUser.builder(userName).buildEmail(email).buildName(firstName, LastName).build();

    return restUtils.doPost("/scim/Users/", lennon).extract().as(ScimUser.class);
  }

  private ScimUserPatchRequest getPatchAddRequest(ScimUser updates) {

    return ScimUserPatchRequest.builder().add(updates).build();
  }

  private ScimUserPatchRequest getPatchRemoveRequest(ScimUser updates) {

    return ScimUserPatchRequest.builder().remove(updates).build();
  }

  private ScimUserPatchRequest getPatchReplaceRequest(ScimUser updates) {

    return ScimUserPatchRequest.builder().replace(updates).build();
  }

  @Test
  public void testPatchUserInfo() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

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

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("userName", equalTo(lennon_update.getUserName()))
      .body("displayName", equalTo(lennon_update.getUserName()))
      .body("name.givenName", equalTo(name.getGivenName()))
      .body("name.familyName", equalTo(name.getFamilyName()))
      .body("name.middleName", equalTo(name.getMiddleName()))
      .body("name.formatted", equalTo(name.getFormatted()))
      .body("active", equalTo(lennon_update.getActive()))
      .body("emails", hasSize(equalTo(1)))
      .body("emails[0].value", equalTo(lennon_update.getEmails().get(0).getValue()))
      .body("addresses", hasSize(equalTo(1)))
      .body("addresses[0].formatted", equalTo(address.getFormatted()))
      .body("addresses[0].streetAddress", equalTo(address.getStreetAddress()))
      .body("addresses[0].locality", equalTo(address.getLocality()))
      .body("addresses[0].region", equalTo(address.getRegion()))
      .body("addresses[0].postalCode", equalTo(address.getPostalCode()))
      .body("addresses[0].country", equalTo(address.getCountry()));

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void testAddRessignAndRemoveOidcId() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    ScimUser lincoln = addTestUser("abraham_lincoln", "lincoln@email.test", "Abraham", "Lincoln");

    ScimIndigoUser indigoUser = ScimIndigoUser.builder()
      .addOidcid(ScimOidcId.builder().issuer("test_issuer").subject("test_subject").build())
      .build();

    ScimUser updateOidcId = ScimUser.builder().indigoUserInfo(indigoUser).build();

    ScimUserPatchRequest req = getPatchAddRequest(updateOidcId);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.oidcIds[0].issuer",
          equalTo(indigoUser.getOidcIds().get(0).getIssuer()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.oidcIds[0].subject",
          equalTo(indigoUser.getOidcIds().get(0).getSubject()));

    /* lincoln tryes to add the oidc account: */
    restUtils.doPatch(lincoln.getMeta().getLocation(), req, HttpStatus.CONFLICT);

    /* Remove oidc account */
    req = getPatchRemoveRequest(updateOidcId);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation()).body("id", equalTo(lennon.getId())).body(
        "urn:indigo-dc:scim:schemas:IndigoUser", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(lincoln.getMeta().getLocation());
  }

  @Test
  public void testRemoveNotExistingOidcId() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .indigoUserInfo(ScimIndigoUser.builder()
        .addOidcid(ScimOidcId.builder().issuer("test_issuer").subject("test_subject").build())
        .build())
      .build();

    ScimUserPatchRequest req = getPatchRemoveRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.NOT_FOUND);

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void testAddInvalidBase64X509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value("this is not a certificate")
        .accountRef(TestUtils.getMemberRef(lennon))
        .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.BAD_REQUEST);

    restUtils.doDelete(lennon.getMeta().getLocation());
  }
  
  @Test
  public void testAddInvalidX509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    String certificate = Base64.getEncoder().encodeToString("this is not a certificate".getBytes());

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value(certificate)
        .accountRef(TestUtils.getMemberRef(lennon))
        .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.BAD_REQUEST);

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void testAddAndRemoveX509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value(TestUtils.getX509TestCertificate())
        .accountRef(TestUtils.getMemberRef(lennon))
        .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("x509Certificates", hasSize(equalTo(1)))
      .body("x509Certificates[0].value",
          equalTo(lennon_update.getX509Certificates().get(0).getValue()));

    ScimUser lennon_remove = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder().value(TestUtils.getX509TestCertificate()).build())
      .build();

    req = getPatchRemoveRequest(lennon_remove);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.NO_CONTENT);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("x509Certificates", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void testPatchUserPassword() {

    ScimUser user = ScimUser.builder("user_with_password")
      .buildEmail("up@test.org")
      .buildName("User", "With Password")
      .password("a_password")
      .active(true)
      .build();

    ScimUser creationResult = restUtils.doPost("/scim/Users/", user)
      .extract()
      .as(ScimUser.class);

    assertNull(creationResult.getPassword());

    passwordTokenGetter().scope("openid")
      .username("user_with_password")
      .password("a_password")
      .getAccessToken();

    ScimUser patchedPasswordUser = ScimUser.builder().password("new_password").build();

    restUtils.doPatch(creationResult.getMeta().getLocation(), ScimUserPatchRequest.builder().add(patchedPasswordUser).build());

    passwordTokenGetter().scope("openid")
      .username("user_with_password")
      .password("new_password")
      .getAccessToken();

    restUtils.doDelete(creationResult.getMeta().getLocation());
  }

  @Test
  public void testAddAndRemoveSshKey() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .indigoUserInfo(ScimIndigoUser.builder()
          .addSshKey(ScimSshKey.builder()
              .display("Personal rsa key")
              .value(TestUtils.getSshKey())
              .build())
          .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].display",
          equalTo(lennon_update.getIndigoUser().getSshKeys().get(0).getDisplay()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].value",
          equalTo(lennon_update.getIndigoUser().getSshKeys().get(0).getValue()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].fingerprint",
          equalTo(TestUtils.getSshKeySHA256Fingerprint()));

    req = getPatchRemoveRequest(ScimUser.builder()
        .indigoUserInfo(ScimIndigoUser.builder()
            .addSshKey(ScimSshKey.builder()
                .display("Personal rsa key")
                .build())
            .build())
        .build());

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.NO_CONTENT);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
  }
  
  @Test
  public void testSshKeyCreateWithKeyAndRemoveWithFingerprint() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .indigoUserInfo(ScimIndigoUser.builder()
          .addSshKey(ScimSshKey.builder()
              .display("Personal rsa key")
              .value(TestUtils.getSshKey())
              .build())
          .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].display",
          equalTo(lennon_update.getIndigoUser().getSshKeys().get(0).getDisplay()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].value",
          equalTo(lennon_update.getIndigoUser().getSshKeys().get(0).getValue()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser.sshKeys[0].fingerprint",
          equalTo(TestUtils.getSshKeySHA256Fingerprint()));

    req = getPatchRemoveRequest(ScimUser.builder()
        .indigoUserInfo(ScimIndigoUser.builder()
            .addSshKey(ScimSshKey.builder()
                .fingerprint(TestUtils.getSshKeySHA256Fingerprint())
                .build())
            .build())
        .build());

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.NO_CONTENT);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("urn:indigo-dc:scim:schemas:IndigoUser", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
  }
}
