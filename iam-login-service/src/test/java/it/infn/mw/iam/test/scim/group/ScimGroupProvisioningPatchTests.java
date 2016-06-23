package it.infn.mw.iam.test.scim.group;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
import it.infn.mw.iam.util.JacksonUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningPatchTests {

  public static final String SCIM_CONTENT_TYPE = "application/scim+json";

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

  private ScimGroup addTestGroup(String displayName) {

    String uuid = UUID.randomUUID().toString();

    ScimGroup group = ScimGroup.builder(displayName).id(uuid).build();

    return restUtils.doPost("/scim/Groups/", group).extract().as(ScimGroup.class);
  }

  private ScimUser addTestUser(String userName, String email, String firstName, String LastName) {

    ScimUser lennon =
        ScimUser.builder(userName).buildEmail(email).buildName(firstName, LastName).build();

    return restUtils.doPost("/scim/Users/", lennon).extract().as(ScimUser.class);
  }

  private ScimGroupPatchRequest getPatchAddUsersRequest(List<ScimUser> users) {

    return ScimGroupPatchRequest.builder().add(TestUtils.buildScimMemberRefList(users)).build();
  }

  private ScimGroupPatchRequest getPatchRemoveUsersRequest(List<ScimUser> users) {

    return ScimGroupPatchRequest.builder().remove(TestUtils.buildScimMemberRefList(users)).build();
  }

  private ScimGroupPatchRequest getPatchRemoveAllUsersRequest() {

    return ScimGroupPatchRequest.builder().remove(null).build();
  }

  private ScimGroupPatchRequest getPatchReplaceUsersRequest(List<ScimUser> users) {

    return ScimGroupPatchRequest.builder().replace(TestUtils.buildScimMemberRefList(users)).build();
  }

  @Test
  public void testAddMemberToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    members.add(lennon);

    ScimGroupPatchRequest patchReq = getPatchAddUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lennon.getDisplayName()))
      .body("members[0].value", equalTo(lennon.getId()))
      .body("members[0].$ref", equalTo(lennon.getMeta().getLocation()));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  @Test
  public void testRemoveNonMemberFromGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    members.add(lennon);

    ScimGroupPatchRequest patchReq = getPatchRemoveUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReq);

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  @Test
  public void testAddAndRemoveMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    ScimUser lincoln = addTestUser("abraham_lincoln", "lincoln@email.test", "Abraham", "Lincoln");
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchAddUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchAddReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(2)));

    ScimGroupPatchRequest patchRemoveReq = getPatchRemoveUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchRemoveReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(lincoln.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  @Test
  public void testAddMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test", "Abhram", "Lincoln");
    restUtils.doDelete(lincoln.getMeta().getLocation());
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchAddUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchAddReq, HttpStatus.NOT_FOUND);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  @Test
  public void testRemoveMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test", "Abhram", "Lincoln");
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchAddUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchAddReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(2)));

    ScimGroupPatchRequest patchRemoveReq = getPatchRemoveAllUsersRequest();

    restUtils.doPatch(engineers.getMeta().getLocation(), patchRemoveReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(lincoln.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

  @Test
  public void testReplaceMember() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John", "Lennon");
    members.add(lennon);

    List<ScimUser> replacedMembers = new ArrayList<ScimUser>();
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test", "Abhram", "Lincoln");
    replacedMembers.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchAddUsersRequest(members);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchAddReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lennon.getUserName()));

    ScimGroupPatchRequest patchReplaceReq = getPatchReplaceUsersRequest(replacedMembers);

    restUtils.doPatch(engineers.getMeta().getLocation(), patchReplaceReq);

    restUtils.doGet(engineers.getMeta().getLocation())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lincoln.getUserName()));

    restUtils.doDelete(lennon.getMeta().getLocation());
    restUtils.doDelete(lincoln.getMeta().getLocation());
    restUtils.doDelete(engineers.getMeta().getLocation());
  }

}
