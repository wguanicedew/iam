package it.infn.mw.iam.scim.user;

import java.util.Base64;

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
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.scim.ScimRestUtils;
import it.infn.mw.iam.scim.TestUtils;

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
  public void patchUserInfo() {

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
  public void patchAddRessignAndRemoveOidcId() {

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
  public void patchRemoveNotExistingOidcId() {

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
  public void patchAddInvalidBase64X509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value("this is not a certificate")
        .accountRef(getMemberRef(lennon))
        .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.BAD_REQUEST);

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void patchAddInvalidX509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    String certificate = Base64.getEncoder().encodeToString("this is not a certificate".getBytes());

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value(certificate)
        .accountRef(getMemberRef(lennon))
        .build())
      .build();

    ScimUserPatchRequest req = getPatchAddRequest(lennon_update);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.BAD_REQUEST);

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  @Test
  public void patchAddAndRemoveX509Certificate() {

    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");

    ScimUser lennon_update = ScimUser.builder()
      .addX509Certificate(ScimX509Certificate.builder()
        .primary(true)
        .display("Personal Certificate")
        .value(getX509TestCertificate())
        .accountRef(getMemberRef(lennon))
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
      .addX509Certificate(ScimX509Certificate.builder().value(getX509TestCertificate()).build())
      .build();

    req = getPatchRemoveRequest(lennon_remove);

    restUtils.doPatch(lennon.getMeta().getLocation(), req, HttpStatus.NO_CONTENT);

    restUtils.doGet(lennon.getMeta().getLocation())
      .body("id", equalTo(lennon.getId()))
      .body("userName", equalTo(lennon.getUserName()))
      .body("x509Certificates", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
  }

  private ScimMemberRef getMemberRef(ScimUser user) {

    return ScimMemberRef.builder()
      .display(user.getDisplayName())
      .ref(user.getMeta().getLocation())
      .value(user.getId())
      .build();
  }

  private String getX509TestCertificate() {

    return "MIIEWDCCA0CgAwIBAgIDAII4MA0GCSqGSIb3DQEBCwUAMC4xCzAJBgNVBAYTAklU"
        + "MQ0wCwYDVQQKEwRJTkZOMRAwDgYDVQQDEwdJTkZOIENBMB4XDTE1MDUxODEzNTQx"
        + "NFoXDTE2MDUxNzEzNTQxNFowZDELMAkGA1UEBhMCSVQxDTALBgNVBAoTBElORk4x"
        + "HTAbBgNVBAsTFFBlcnNvbmFsIENlcnRpZmljYXRlMQ0wCwYDVQQHEwRDTkFGMRgw"
        + "FgYDVQQDEw9FbnJpY28gVmlhbmVsbG8wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAw"
        + "ggEKAoIBAQDf74gCX/5D7HAKlI9u+vMy4R8uYvtZp60L401zOuDHc0sKPCq2sU8N"
        + "IB8cNOC+69h+hPqbU8gcleXZ0T3KOy3NPrU7CFaOxzsCVAoDcLeKFlCMu4X1OK0V"
        + "NPq7+fgJ1cVdsJ4StHl3oTtQPCoU6NNly8HJIufVjat2IgjNHdMHINs5IcxpTmE5"
        + "OGae3reOfRBtqBr8UvyiTwHEEll6JpdbKjzjrcHBoOdFZTiwR18fO+B8MZLOjXSk"
        + "OEG5p5K8y4UOkHQeqooKgW0tn7dvCxQfuu5TGYUmK6pwjcxzcnSE9U4abFh5/oD1"
        + "PqjoCGtlvnl9nGrhAFD+qa5zq6SrgWsNAgMBAAGjggFHMIIBQzAMBgNVHRMBAf8E"
        + "AjAAMA4GA1UdDwEB/wQEAwIEsDAdBgNVHSUEFjAUBggrBgEFBQcDAgYIKwYBBQUH"
        + "AwQwPQYDVR0fBDYwNDAyoDCgLoYsaHR0cDovL3NlY3VyaXR5LmZpLmluZm4uaXQv"
        + "Q0EvSU5GTkNBX2NybC5kZXIwJQYDVR0gBB4wHDAMBgorBgEEAdEjCgEHMAwGCiqG"
        + "SIb3TAUCAgEwHQYDVR0OBBYEFIQEiwCbKssJqSBNMziZtu54ZQRCMFYGA1UdIwRP"
        + "ME2AFNFi87N3csgu+/J5Gm83TiefE9UgoTKkMDAuMQswCQYDVQQGEwJJVDENMAsG"
        + "A1UEChMESU5GTjEQMA4GA1UEAxMHSU5GTiBDQYIBADAnBgNVHREEIDAegRxlbnJp"
        + "Y28udmlhbmVsbG9AY25hZi5pbmZuLml0MA0GCSqGSIb3DQEBCwUAA4IBAQBfhv9P"
        + "4bYo7lVRYjHrxreKVaEyujzPZFowZPYMz0e/lPcdqh9TIoDBbhy7/PXiTVqQEniZ"
        + "fU1Nso4rqBj8Qy609Y60PEFHhfLnjhvd/d+pXu6F1QTzUMwA2k7z5M+ykh7L46/z"
        + "1vwvcdvCgtWZ+FedvLuKh7miTCfxEIRLcpRPggbC856BSKet7jPdkMxkUwbFa34Z"
        + "qOuDQ6MvcrFA/lLgqN1c1OoE9tnf/uyOjVYq8hyXqOAhi2heE1e+s4o3/PQsaP5x"
        + "LetVho/J33BExHo+hCMt1rN89DO5qU7FFijLlbmOZROacpjkPNn2V4wkd5WeX2dm" + "b6UoBRqPsAiQL0mY";
  }
}
