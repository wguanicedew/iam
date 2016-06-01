package it.infn.mw.iam.scim.group;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.matcher.ResponseAwareMatcherComposer.and;
import static com.jayway.restassured.matcher.RestAssuredMatchers.endsWithPath;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

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
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningTests {

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

  private void deleteGroup(String groupLocation) {

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).when().delete(groupLocation).then()
	  .statusCode(HttpStatus.NO_CONTENT.value());

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).when().get(groupLocation).then()
	  .statusCode(HttpStatus.NOT_FOUND.value());

  }

  @Test
  public void testGetGroupNotFoundResponse() {

	String randomUuid = UUID.randomUUID().toString();

	given().port(8080).auth().preemptive().oauth2(accessToken).when()
	  .get("/scim/Groups/" + randomUuid).then().log().body(true)
	  .statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo("404"))
	  .body("detail", equalTo("No group mapped to id '" + randomUuid + "'"))
	  .contentType(SCIM_CONTENT_TYPE);

  }

  @Test
  public void testUpdateGroupNotFoundResponse() {

	ScimGroup group = ScimGroup.builder("engineers").build();

	String randomUuid = UUID.randomUUID().toString();

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).body(group).when()
	  .put("/scim/Groups/" + randomUuid).then().log().body(true)
	  .statusCode(HttpStatus.NOT_FOUND.value()).body("status", equalTo("404"))
	  .body("detail", equalTo("No group mapped to id '" + randomUuid + "'"))
	  .contentType(SCIM_CONTENT_TYPE);

  }

  @SuppressWarnings("unchecked")
  @Test
  public void testExistingGroupAccess() {

	// Some existing group as defined in the test db
	String groupId = "c617d586-54e6-411d-8e38-64967798fa8a";

	given().port(8080).auth().preemptive().oauth2(accessToken).when()
	  .get("/scim/Groups/" + groupId).then().contentType(SCIM_CONTENT_TYPE)
	  .log().body(true).statusCode(HttpStatus.OK.value())
	  .body("id", equalTo(groupId)).body("displayName", equalTo("Production"))
	  .body("meta.resourceType", equalTo("Group"))
	  .body("meta.location",
		equalTo("http://localhost:8080/scim/Groups/" + groupId))
	  .body("members", hasSize(equalTo(2)))
	  .body("members[0].$ref",
		and(startsWith("http://localhost:8080/scim/Users/"),
		  endsWithPath("members[0].value")))
	  .body("members[1].$ref",
		and(startsWith("http://localhost:8080/scim/Users/"),
		  endsWithPath("members[1].value")))
	  .body("schemas", contains(ScimGroup.GROUP_SCHEMA));

  }

  @Test
  public void testGroupCreationAccessDeletion() {

	String name = "engineers";
	String uuid = UUID.randomUUID().toString();

	ScimGroup.Builder builder = new ScimGroup.Builder(name);
	builder.id(uuid);

	ScimGroup group = builder.build();

	ScimGroup createdGroup = given().port(8080).auth().preemptive()
	  .oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).body(group).log()
	  .all(true).when().post("/scim/Groups/").then().log().all(true)
	  .statusCode(HttpStatus.CREATED.value()).extract().as(ScimGroup.class);

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).when()
	  .get(createdGroup.getMeta().getLocation()).then().log().all(true)
	  .statusCode(HttpStatus.OK.value())
	  .body("id", equalTo(createdGroup.getId()))
	  .body("displayName", equalTo(createdGroup.getDisplayName()));

	deleteGroup(createdGroup.getMeta().getLocation());
  }

  @Test
  public void testGroupUpdateChangeDisplayname() {

	String uuid = UUID.randomUUID().toString();

	ScimGroup group = ScimGroup.builder("engineers")
	  .id(uuid)
	  .build();

	ScimGroup createdGroup = given().port(8080).auth().preemptive()
	  .oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).body(group).log()
	  .all(true).when().post("/scim/Groups/").then().log().all(true)
	  .statusCode(HttpStatus.CREATED.value()).extract().as(ScimGroup.class);

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).when()
	  .get(createdGroup.getMeta().getLocation()).then().log().all(true)
	  .statusCode(HttpStatus.OK.value())
	  .body("id", equalTo(createdGroup.getId()))
	  .body("displayName", equalTo(createdGroup.getDisplayName()));

	ScimGroup updatedGroup = ScimGroup.builder("engineers_updated")
	  .id(uuid)
	  .meta(createdGroup.getMeta())
	  .build();

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).body(updatedGroup).log().all(true).when()
	  .put(updatedGroup.getMeta().getLocation()).then().log().all(true)
	  .statusCode(HttpStatus.OK.value())
	  .body("id", equalTo(updatedGroup.getId()))
	  .body("displayName", equalTo(updatedGroup.getDisplayName()));

	deleteGroup(createdGroup.getMeta().getLocation());
  }

  @Test
  public void testEmptyDisplayNameValidationError() {

	String displayName = "";
	String uuid = UUID.randomUUID().toString();

	ScimGroup group = ScimGroup.builder(displayName).id(uuid).build();

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).body(group).log().all(true).when()
	  .post("/scim/Groups/").then()
	  .body("detail", containsString("scimGroup.displayName : may not be empty"))
	  .log().all(true).statusCode(HttpStatus.BAD_REQUEST.value());

  }
  
  @Test
  public void testGroupUpdateChangeWithInvalidDisplayname() {

	String uuid = UUID.randomUUID().toString();

	ScimGroup group = ScimGroup.builder("engineers")
	  .id(uuid)
	  .build();

	ScimGroup createdGroup = given().port(8080).auth().preemptive()
	  .oauth2(accessToken).contentType(SCIM_CONTENT_TYPE).body(group).log()
	  .all(true).when().post("/scim/Groups/").then().log().all(true)
	  .statusCode(HttpStatus.CREATED.value()).extract().as(ScimGroup.class);

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).when()
	  .get(createdGroup.getMeta().getLocation()).then().log().all(true)
	  .statusCode(HttpStatus.OK.value())
	  .body("id", equalTo(createdGroup.getId()))
	  .body("displayName", equalTo(createdGroup.getDisplayName()));

	ScimGroup updatedGroup = ScimGroup.builder("")
	  .id(uuid)
	  .meta(createdGroup.getMeta())
	  .build();

	given().port(8080).auth().preemptive().oauth2(accessToken)
	  .contentType(SCIM_CONTENT_TYPE).body(updatedGroup).log().all(true).when()
	  .put(updatedGroup.getMeta().getLocation()).then().log().all(true)
	  .statusCode(HttpStatus.BAD_REQUEST.value());

	deleteGroup(createdGroup.getMeta().getLocation());
  }
  
}
