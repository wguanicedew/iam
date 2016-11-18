package it.infn.mw.iam.test.scim.group.patch;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;

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
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimGroupPatchRequest;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.test.ScimRestUtils;
import it.infn.mw.iam.test.TestUtils;
import it.infn.mw.iam.test.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningPatchRemoveTests {

  public static final String SCIM_CONTENT_TYPE = "application/scim+json";

  private String accessToken;
  private ScimRestUtils restUtils;

  private ScimGroup engineers;
  private ScimUser lennon;
  private ScimUser lincoln;
  private ScimUser kennedy;

  List<ScimUser> members;

  @BeforeClass
  public static void init() {

    JacksonUtils.initRestAssured();
  }

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read scim:write");
    restUtils = ScimRestUtils.getInstance(accessToken);

    engineers = addTestGroup("engineers");
    lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    lincoln = addTestUser("abraham_lincoln", "lincoln@email.test", "Abraham", "Lincoln");
    kennedy = addTestUser("jfk", "jfk@whitehouse.us", "John", "Kennedy");

    members = new ArrayList<ScimUser>();
    members.add(lennon);
    members.add(lincoln);
    members.add(kennedy);

    addMembers(engineers, members);
  }

  @After
  public void teardownTests() {

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(lincoln.getMeta().getLocation());
    restUtils.doDelete(kennedy.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  private ScimGroup addTestGroup(String displayName) {

    ScimGroup group = ScimGroup.builder(displayName).build();

    return restUtils.doPost("/scim/Groups/", group).extract().as(ScimGroup.class);
  }

  private ScimUser addTestUser(String userName, String email, String firstName, String LastName) {

    ScimUser lennon =
        ScimUser.builder(userName).buildEmail(email).buildName(firstName, LastName).build();

    return restUtils.doPost("/scim/Users/", lennon).extract().as(ScimUser.class);
  }

  private void addMembers(ScimGroup group, List<ScimUser> members) {

    ScimGroupPatchRequest patchAddReq = ScimGroupPatchUtils.getPatchAddUsersRequest(members);

    restUtils.doPatch(group.getMeta().getLocation(), patchAddReq);

    ScimGroup g = restUtils.doGet(group.getMeta().getLocation()).extract().as(ScimGroup.class);

    Assert.assertTrue(g.getMembers().contains(TestUtils.getMemberRef(members.get(0))));
    Assert.assertTrue(g.getMembers().contains(TestUtils.getMemberRef(members.get(1))));
    Assert.assertTrue(g.getMembers().contains(TestUtils.getMemberRef(members.get(2))));
  }

  @Test
  public void testGroupPatchRemoveMember() {

    List<ScimUser> membersToRemove = new ArrayList<ScimUser>();
    membersToRemove.add(lennon);

    ScimGroupPatchRequest patchReq =
        ScimGroupPatchUtils.getPatchRemoveUsersRequest(membersToRemove);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    ScimGroup updatedGroup = restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(2)))
      .extract()
      .as(ScimGroup.class);

    Assert.assertTrue(updatedGroup.getMembers().contains(TestUtils.getMemberRef(lincoln)));
    Assert.assertTrue(updatedGroup.getMembers().contains(TestUtils.getMemberRef(kennedy)));
  }

  @Test
  public void testGroupPatchRemoveMultipleMembers() {

    List<ScimUser> membersToRemove = new ArrayList<ScimUser>();
    membersToRemove.add(lennon);
    membersToRemove.add(lincoln);

    ScimGroupPatchRequest patchReq =
        ScimGroupPatchUtils.getPatchRemoveUsersRequest(membersToRemove);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    ScimGroup updatedGroup = restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .extract()
      .as(ScimGroup.class);

    Assert.assertTrue(updatedGroup.getMembers().contains(TestUtils.getMemberRef(kennedy)));
  }

  @Test
  public void testGroupPatchRemoveAllListOfMembers() {

    List<ScimUser> membersToRemove = new ArrayList<ScimUser>();
    membersToRemove.add(lennon);
    membersToRemove.add(lincoln);
    membersToRemove.add(kennedy);

    ScimGroupPatchRequest patchReq =
        ScimGroupPatchUtils.getPatchRemoveUsersRequest(membersToRemove);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    ScimGroup updatedGroup = restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .extract()
      .as(ScimGroup.class);

    Assert.assertTrue(updatedGroup.getMembers().isEmpty());
  }

  @Test
  public void testGroupPatchRemoveAllMembers() {

    List<ScimUser> emptyMembers = new ArrayList<ScimUser>();

    ScimGroupPatchRequest patchReq = ScimGroupPatchUtils.getPatchRemoveUsersRequest(emptyMembers);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    ScimGroup updatedGroup = restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .extract()
      .as(ScimGroup.class);

    Assert.assertTrue(updatedGroup.getMembers().isEmpty());
  }

  @Test
  public void testGroupPatchRemoveNonMember() {

    List<ScimUser> members = new ArrayList<ScimUser>();
    members.add(lennon);

    ScimGroupPatchRequest patchReq = ScimGroupPatchUtils.getPatchRemoveUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    ScimGroup updatedGroup =
        restUtils.doGet(engineers.getMeta().getLocation()).extract().as(ScimGroup.class);

    Assert.assertFalse(updatedGroup.getMembers().contains(lennon));

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq, HttpStatus.NOT_FOUND);
  }

}
