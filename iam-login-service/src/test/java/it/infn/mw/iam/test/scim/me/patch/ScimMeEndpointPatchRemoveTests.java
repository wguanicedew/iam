package it.infn.mw.iam.test.scim.me.patch;

import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;

import org.junit.After;
import org.junit.Assert;
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
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimPhoto;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.test.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimMeEndpointPatchRemoveTests {

  private ScimRestUtils userRestUtils;
  private ScimRestUtils adminRestUtils;
  private ScimUser testUser;

  final String TESTUSER_USERNAME = "patchRemoveUser";
  final String TESTUSER_PASSWORD = "password";
  final ScimName TESTUSER_NAME = ScimName.builder().givenName("John").familyName("Lennon").build();
  final ScimEmail TESTUSER_EMAIL = ScimEmail.builder().email("john.lennon@liverpool.uk").build();
  final ScimPhoto TESTUSER_PHOTO = ScimPhoto.builder().value("http://site.org/user.png").build();
  final ScimAddress TESTUSER_ADDRESS = ScimAddress.builder()
    .country("IT")
    .formatted("viale Berti Pichat 6/2\nBologna IT")
    .locality("Bologna")
    .postalCode("40121")
    .region("Emilia Romagna")
    .streetAddress("viale Berti Pichat")
    .build();

  @BeforeClass
  public static void init() {

    JacksonUtils.initRestAssured();
  }

  @Before
  public void testSetup() {

    adminRestUtils = ScimRestUtils
      .getInstance(TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read scim:write"));

    testUser = adminRestUtils
      .doPost("/scim/Users/",
          ScimUser.builder()
            .active(true)
            .userName(TESTUSER_USERNAME)
            .password(TESTUSER_PASSWORD)
            .addEmail(TESTUSER_EMAIL)
            .name(TESTUSER_NAME)
            .build())
      .extract().as(ScimUser.class);

    userRestUtils = ScimRestUtils.getInstance(passwordTokenGetter().username(TESTUSER_USERNAME)
      .password(TESTUSER_PASSWORD)
      .getAccessToken());
  }

  @After
  public void testTeardown() {

    adminRestUtils.doDelete(testUser.getMeta().getLocation());
  }

  private ScimUser doGet() {

    return userRestUtils.doGet("/scim/Me").extract().as(ScimUser.class);
  }

  private void doPatch(ScimUserPatchRequest patchRequest) {

    userRestUtils.doPatch("/scim/Me", patchRequest);
  }

  @Test
  public void testPatchRemovePicture() {

    final ScimUserPatchRequest patchAddRequest = ScimUserPatchRequest.builder()
      .add(ScimUser.builder().addPhoto(TESTUSER_PHOTO).build())
      .build();

    doPatch(patchAddRequest);

    ScimPhoto updatedPhoto = doGet().getPhotos().get(0);

    Assert.assertTrue(updatedPhoto.equals(TESTUSER_PHOTO));

    ScimUserPatchRequest patchRemoveRequest = ScimUserPatchRequest.builder()
      .remove(ScimUser.builder().addPhoto(TESTUSER_PHOTO).build())
      .build();

    doPatch(patchRemoveRequest);

    ScimUser updatedUser = doGet();

    Assert.assertFalse(updatedUser.hasPhotos());
  }

  @Test
  public void testPatchRemoveAddress() {

    final ScimUserPatchRequest patchAddRequest = ScimUserPatchRequest.builder()
      .add(ScimUser.builder().addAddress(TESTUSER_ADDRESS).build())
      .build();

    doPatch(patchAddRequest);

    ScimAddress updatedAddress = doGet().getAddresses().get(0);

    Assert.assertTrue(updatedAddress.equals(TESTUSER_ADDRESS));

    ScimUserPatchRequest patchRemoveRequest = ScimUserPatchRequest.builder()
      .remove(ScimUser.builder().addAddress(TESTUSER_ADDRESS).build())
      .build();

    doPatch(patchRemoveRequest);

    ScimUser updatedUser = doGet();

    Assert.assertFalse(updatedUser.hasAddresses());
  }

  @Test
  public void testPatchRemoveAddressNotExists() {

    final ScimUserPatchRequest patchRemoveRequest = ScimUserPatchRequest.builder()
      .remove(ScimUser.builder().addAddress(TESTUSER_ADDRESS).build())
      .build();

    userRestUtils.doPatch("/scim/Me", patchRemoveRequest, HttpStatus.NOT_FOUND);
  }
}
