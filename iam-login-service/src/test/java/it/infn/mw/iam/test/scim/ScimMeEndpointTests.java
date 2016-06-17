package it.infn.mw.iam.test.scim;

import static com.jayway.restassured.RestAssured.given;
import static it.infn.mw.iam.api.scim.model.ScimConstants.SCIM_CONTENT_TYPE;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class ScimMeEndpointTests {

  String accessToken;

  @Before
  public void initAccessToken() {

    accessToken = TestUtils.getAccessToken("scim-client-rw", "secret", "scim:read");
  }

  @Test
  public void meEndpointReturns501() {

    given().port(8080).auth().preemptive().oauth2(accessToken).accept(SCIM_CONTENT_TYPE).log()
        .all(true).when().get("/scim/Me").then().log().all(true)
        .statusCode(HttpStatus.NOT_IMPLEMENTED.value()).body("status", equalTo("501"))
        .body("detail", equalTo("The /scim/Me endpoint is not implemented"));

  }

}
