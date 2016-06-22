package it.infn.mw.iam.scim.group;

import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.contains;
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
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.scim.ScimRestUtils;
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimGroupProvisioningListTests {

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

  @Test
  public void testNoParameterListRequest() {

    restUtils.doGet("/scim/Groups")
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(10))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(10)));
  }

  @Test
  public void testCountAs8Returns8Items() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 8)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(8))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(8)));
    /** @formatter:on */
  }

  @Test
  public void testCount1Returns1Item() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 1)
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
      .body("Resources", hasSize(equalTo(1)));
    /** @formatter:on */
  }

  @Test
  public void testCountShouldBeLimitedToTen() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 30)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(10))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(10)));
    /** @formatter:on */
  }

  @Test
  public void testNegativeCountBecomesZero() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", -10)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(0))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(0)));
    /** @formatter:on */
  }

  @Test
  public void testInvalidStartIndex() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",23)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(0))
      .body("startIndex", equalTo(23))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(0)));
    /** @formatter:on */
  }

  @Test
  public void testRightEndPagination() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",17)
      .param("count",10)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(6))
      .body("startIndex", equalTo(17))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(6)));
    /** @formatter:on */
  }

  @Test
  public void testLastElementPagination() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",22)
      .param("count",2)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(1))
      .body("startIndex", equalTo(22))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(1)));
    /** @formatter:on */
  }

  @Test
  public void testFirstElementPagination() {

    /** @formatter:off */
    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",1)
      .param("count",5)
      .when()
      .get("/scim/Groups")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(22))
      .body("itemsPerPage", equalTo(5))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(5)));
    /** @formatter:on */
  }
}
