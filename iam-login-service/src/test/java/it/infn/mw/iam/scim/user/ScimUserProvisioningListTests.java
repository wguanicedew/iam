package it.infn.mw.iam.scim.user;

import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.scim.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimUserProvisioningListTests {

  String accessToken;
  
  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret",
      "scim:read");
  }

  
  @Test
  public void testNoParameterListRequest() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(100))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(100)));
  }
  
  @Test
  public void testCountAs10Returns10Items() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 10)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(10))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(10)));
  }
  
  @Test
  public void testCount1Returns1Item() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 1)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(1))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(1)));
  }
  
  @Test
  public void testCountShouldBeLimitedToOneHundred() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", 1000)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(100))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(100)));
  }
  
  @Test
  public void testNegativeCountBecomesZero() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("count", -10)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(0))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(0)));
  }
  
  @Test
  public void testInvalidStartIndex() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",251)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(0))
      .body("startIndex", equalTo(251))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(0)));
  }
  
  @Test
  public void testRightEndPagination() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",245)
      .param("count",10)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(6))
      .body("startIndex", equalTo(245))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(6)));
  }
  
  @Test
  public void testLastElementPagination() {

    given().port(8080)
      .auth()
      .preemptive()
      .oauth2(accessToken)
      .accept(SCIM_CONTENT_TYPE)
      .log()
      .all(true)
      .param("startIndex",250)
      .param("count",2)
      .when()
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(1))
      .body("startIndex", equalTo(250))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(1)));
  }
  
  @Test
  public void testFirstElementPagination() {

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
      .get("/scim/Users")
      .then()
      .log()
      .all(true)
      .statusCode(HttpStatus.OK.value())
      .body("totalResults", equalTo(250))
      .body("itemsPerPage", equalTo(5))
      .body("startIndex", equalTo(1))
      .body("schemas", contains(ScimListResponse.SCHEMA))
      .body("Resources", hasSize(equalTo(5)));
  }
}
