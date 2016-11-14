package it.infn.mw.iam.test.scim.me.patch;

import static it.infn.mw.iam.test.TestUtils.passwordTokenGetter;

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
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimMeEndpointPatchRemoveTests {

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
  public void testPatchPicture() {

    final String PICTURE = "http://notarealurl.com/image.jpg";

    ScimUserPatchRequest patchRequest =
        ScimUserPatchRequest.builder().add(ScimUser.builder().buildPhoto(PICTURE).build()).build();

    doPatch(patchRequest);

    patchRequest =
        ScimUserPatchRequest.builder().remove(ScimUser.builder().buildPhoto(PICTURE).build()).build();

    doPatch(patchRequest);

    ScimUser updatedUser = doGet();

    Assert.assertNull(updatedUser.getPhotos());
  }
}
