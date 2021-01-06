/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.test.login;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest(randomPort = true)
@TestPropertySource(properties = {"iam.registration.show-registration-button-in-login-page=false"})
public class RegistrationButtonDisabledTests {

  @Value("${local.server.port}")
  private Integer serverPort;

  @BeforeClass
  public static void init() {
    TestUtils.initRestAssured();
  }

  @Test
  public void registrationButtonIsNotShown() {
    RestAssured.given()
      .port(serverPort)
      .log()
      .all(true)
      .when()
      .get("/login")
      .then()
      .statusCode(200)
      .body(not(containsString("Apply for an account")));
  }

}
