package it.infn.mw.iam.test.core;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment  = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IamErrorControllerTests {

    @LocalServerPort
    int port;

    @Before
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void testInternalServerError() throws InterruptedException {
        RestAssured.given().basePath("/error").get("").then().statusCode(500);
    }
}
    

