package it.infn.mw.iam.test.scim.group;

import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningAttributeFilterTests {

  String accessToken;

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read");
  }

  @Test
  public void testReuturnOnlyDisplayNameRequest() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 1)
      .param("attributes", "displayName")
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(1))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(1)))
      .body("Resources[0].id", is(Matchers.not(nullValue())))
      .body("Resources[0].schemas", is(Matchers.not(nullValue())))
      .body("Resources[0].displayName", is(Matchers.not(nullValue())));
  }

  @Test
  public void testMultipleAttrsRequest() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 2)
      .param("attributes", "displayName")
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(2))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(2)))
      .body("Resources[0].id", is(Matchers.not(nullValue())))
      .body("Resources[0].schemas", is(not(nullValue())))
      .body("Resources[0].displayName", is(not(nullValue())));

  }

}
