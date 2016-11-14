package it.infn.mw.iam.test.scim.me.patch;

import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimMeEndpointPatchReplaceTests {

  private ScimRestUtils userRestUtils;
  private ScimUser testUser;

  @Autowired
  private ScimUserProvisioning userService;

  @BeforeClass
  public static void init() {

    JacksonUtils.initRestAssured();
  }

  private ScimUser createTestUser(final String username, final String password,
      final String givenname, final String familyname, final String email) {

    return userService.create(ScimUser.builder()
      .active(true)
      .buildEmail(email)
      .buildName(givenname, familyname)
      .displayName(username)
      .userName(username)
      .password(password)
      .build());
  }

  @Before
  public void testSetup() {

    testUser =
        createTestUser("johnLennon", "password", "John", "Lennon", "john.lennon@liverpool.uk");
    userRestUtils = ScimRestUtils.getInstance(passwordTokenGetter().username(testUser.getUserName())
      .password("password")
      .getAccessToken());
  }

  @After
  public void testTeardown() {

    userService.delete(testUser.getId());
  }

  private ScimUser doGet() {

    return userRestUtils.doGet("/scim/Me").extract().as(ScimUser.class);
  }

  private void doPatch(ScimUserPatchRequest patchRequest) {

    userRestUtils.doPatch("/scim/Me", patchRequest);
  }

  @Test
  public void testPatchPassword() {

    final String NEW_PASSWORD = "newpassword";

    ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder().password(NEW_PASSWORD).build())
      .build();

    doPatch(patchRequest);

    // Verify that password has been changed
    passwordTokenGetter().username(testUser.getUserName()).password(NEW_PASSWORD).getAccessToken();
  }

  @Test
  public void testPatchName() {

    final String GIVEN_NAME = "John";
    final String MIDDLE_NAME = "Fitzgerald";
    final String FAMILY_NAME = "Kennedy";

    ScimName updatedName = ScimName.builder()
      .givenName(GIVEN_NAME)
      .middleName(MIDDLE_NAME)
      .familyName(FAMILY_NAME)
      .build();

    ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder().name(updatedName).build())
      .build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertThat(updatedUser.getName(), Matchers.equalTo(updatedName));
  }

  @Test
  public void testPatchPicture() {

    final String PICTURE = "http://notarealurl.com/image.jpg";

    final ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder().buildPhoto(PICTURE).build())
      .build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertThat(updatedUser.getPhotos().get(0).getValue(), Matchers.equalTo(PICTURE));
  }

  @Test
  public void testPatchEmail() {

    final String EMAIL = "john.kennedy@washington.us";

    final ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder().buildEmail(EMAIL).build())
      .build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertThat(updatedUser.getEmails().get(0).getValue(), Matchers.equalTo(EMAIL));
  }

  @Test
  public void testPatchAddress() {

    final ScimAddress ADDRESS = ScimAddress.builder()
      .country("IT")
      .formatted("viale Berti Pichat 6/2\nBologna IT")
      .locality("Bologna")
      .postalCode("40121")
      .region("Emilia Romagna")
      .streetAddress("viale Berti Pichat")
      .build();

    final ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder().addAddress(ADDRESS).build())
      .build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertThat(updatedUser.getAddresses().get(0).getCountry(),
        Matchers.equalTo(ADDRESS.getCountry()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getLocality(),
        Matchers.equalTo(ADDRESS.getLocality()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getPostalCode(),
        Matchers.equalTo(ADDRESS.getPostalCode()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getRegion(),
        Matchers.equalTo(ADDRESS.getRegion()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getStreetAddress(),
        Matchers.equalTo(ADDRESS.getStreetAddress()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getFormatted(),
        Matchers.equalTo(ADDRESS.getFormatted()));
  }

  @Test
  public void testPatchAll() {

    final String GIVEN_NAME = "John";
    final String MIDDLE_NAME = "Fitzgerald";
    final String FAMILY_NAME = "Kennedy";

    final ScimName updatedName = ScimName.builder()
      .givenName(GIVEN_NAME)
      .middleName(MIDDLE_NAME)
      .familyName(FAMILY_NAME)
      .build();

    final String NEW_PASSWORD = "newpassword";
    final String EMAIL = "john.kennedy@washington.us";
    final String PICTURE = "http://notarealurl.com/image.jpg";

    final ScimAddress ADDRESS = ScimAddress.builder()
      .country("IT")
      .formatted("viale Berti Pichat 6/2\nBologna IT")
      .locality("Bologna")
      .postalCode("40121")
      .region("Emilia Romagna")
      .streetAddress("viale Berti Pichat")
      .build();

    final ScimUserPatchRequest patchRequest = ScimUserPatchRequest.builder()
      .replace(ScimUser.builder()
        .name(updatedName)
        .password(NEW_PASSWORD)
        .buildEmail(EMAIL)
        .addAddress(ADDRESS)
        .buildPhoto(PICTURE)
        .build())
      .build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertThat(updatedUser.getName(), Matchers.equalTo(updatedName));
    Assert.assertThat(updatedUser.getName().getGivenName(),
        Matchers.equalTo(updatedName.getGivenName()));
    Assert.assertThat(updatedUser.getName().getMiddleName(),
        Matchers.equalTo(updatedName.getMiddleName()));
    Assert.assertThat(updatedUser.getName().getFamilyName(),
        Matchers.equalTo(updatedName.getFamilyName()));
    Assert.assertThat(updatedUser.getPhotos().get(0).getValue(), Matchers.equalTo(PICTURE));
    Assert.assertThat(updatedUser.getEmails().get(0).getValue(), Matchers.equalTo(EMAIL));
    Assert.assertThat(updatedUser.getAddresses().get(0), Matchers.equalTo(ADDRESS));
    Assert.assertNotNull(updatedUser.getAddresses().get(0));
    Assert.assertThat(updatedUser.getAddresses().get(0).getCountry(),
        Matchers.equalTo(ADDRESS.getCountry()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getLocality(),
        Matchers.equalTo(ADDRESS.getLocality()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getPostalCode(),
        Matchers.equalTo(ADDRESS.getPostalCode()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getRegion(),
        Matchers.equalTo(ADDRESS.getRegion()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getStreetAddress(),
        Matchers.equalTo(ADDRESS.getStreetAddress()));
    Assert.assertThat(updatedUser.getAddresses().get(0).getFormatted(),
        Matchers.equalTo(ADDRESS.getFormatted()));

    passwordTokenGetter().username(testUser.getUserName()).password(NEW_PASSWORD).getAccessToken();
  }
}
