package it.infn.mw.iam.scim.group;

import static com.jayway.restassured.RestAssured.given;
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
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType;
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningPatchTests {

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

  private ScimGroup addTestGroup(String displayName) {

    String uuid = UUID.randomUUID()
      .toString();

    ScimGroup group = ScimGroup.builder(displayName)
      .id(uuid)
      .build();

    return given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(group)
      .log()
      .all(true)
      .when()
      .post("/scim/Groups/")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.CREATED.value())
      .extract()
      .as(ScimGroup.class);
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

  private ScimMemberRef getMemberRef(ScimUser user) {

    return ScimMemberRef.builder()
      .display(user.getDisplayName())
      .ref(user.getMeta()
        .getLocation())
      .value(user.getId())
      .build();
  }

  private ScimGroupPatchRequest getPatchRequest(ScimPatchOperationType op,
    List<ScimUser> users) {

    List<ScimMemberRef> membersRefs = new ArrayList<ScimMemberRef>();
    for (ScimUser u : users) {
      membersRefs.add(getMemberRef(u));
    }

    ScimPatchOperation<List<ScimMemberRef>> membersOp = (new ScimPatchOperation.Builder<List<ScimMemberRef>>())
      .op(op)
      .path("members")
      .value(membersRefs)
      .build();

    return ScimGroupPatchRequest.builder()
      .addOperation(membersOp)
      .build();
  }

  private ScimGroupPatchRequest getPatchRemoveAllMembersRequest() {

    ScimPatchOperation<List<ScimMemberRef>> membersOp = (new ScimPatchOperation.Builder<List<ScimMemberRef>>())
      .op(ScimPatchOperationType.remove)
      .path("members")
      .value(null)
      .build();

    return ScimGroupPatchRequest.builder()
      .addOperation(membersOp)
      .build();
  }

  private void deleteGroup(String groupLocation) {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .delete(groupLocation)
      .then()
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(groupLocation)
      .then()
      .statusCode(HttpStatus.NOT_FOUND.value());

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
  public void testAddMemberToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    members.add(lennon);

    ScimGroupPatchRequest patchReq = getPatchRequest(ScimPatchOperationType.add,
      members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lennon.getDisplayName()))
      .body("members[0].value", equalTo(lennon.getId()))
      .body("members[0].$ref", equalTo(lennon.getMeta()
        .getLocation()));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

  @Test
  public void testRemoveNonMemberFromGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    members.add(lennon);

    ScimGroupPatchRequest patchReq = getPatchRequest(
      ScimPatchOperationType.remove, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

  @Test
  public void testAddAndRemoveMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    ScimUser lincoln = addTestUser("abraham_lincoln", "lincoln@email.test",
      "Abraham", "Lincoln");
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchRequest(
      ScimPatchOperationType.add, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchAddReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(2)));

    ScimGroupPatchRequest patchRemoveReq = getPatchRequest(
      ScimPatchOperationType.remove, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchRemoveReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteUser(lincoln.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

  @Test
  public void testAddMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test",
      "Abhram", "Lincoln");
    deleteUser(lincoln.getMeta()
      .getLocation());
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchRequest(
      ScimPatchOperationType.add, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchAddReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NOT_FOUND.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

  @Test
  public void testRemoveMultipleMembersToGroup() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test",
      "Abhram", "Lincoln");
    members.add(lennon);
    members.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchRequest(
      ScimPatchOperationType.add, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchAddReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(2)));

    ScimGroupPatchRequest patchRemoveReq = getPatchRemoveAllMembersRequest();

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchRemoveReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", equalTo(null));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteUser(lincoln.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

  @Test
  public void testReplaceMember() {

    ScimGroup engineers = addTestGroup("engineers");

    List<ScimUser> members = new ArrayList<ScimUser>();
    ScimUser lennon = addTestUser("john_lennon", "lennon@email.test", "John",
      "Lennon");
    members.add(lennon);

    List<ScimUser> replacedMembers = new ArrayList<ScimUser>();
    ScimUser lincoln = addTestUser("abhram_lincoln", "lincoln@email.test",
      "Abhram", "Lincoln");
    replacedMembers.add(lincoln);

    ScimGroupPatchRequest patchAddReq = getPatchRequest(
      ScimPatchOperationType.add, members);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchAddReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lennon.getUserName()));

    ScimGroupPatchRequest patchReplaceReq = getPatchRequest(
      ScimPatchOperationType.replace, replacedMembers);

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .body(patchReplaceReq)
      .log()
      .all(true)
      .when()
      .patch(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.NO_CONTENT.value());

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .contentType(SCIM_CONTENT_TYPE)
      .when()
      .get(engineers.getMeta()
        .getLocation())
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("id", equalTo(engineers.getId()))
      .body("displayName", equalTo(engineers.getDisplayName()))
      .body("members", hasSize(equalTo(1)))
      .body("members[0].display", equalTo(lincoln.getUserName()));

    deleteUser(lennon.getMeta()
      .getLocation());
    deleteUser(lincoln.getMeta()
      .getLocation());
    deleteGroup(engineers.getMeta()
      .getLocation());
  }

}
