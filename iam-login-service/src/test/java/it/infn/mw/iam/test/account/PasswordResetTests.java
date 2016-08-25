package it.infn.mw.iam.test.account;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class PasswordResetTests {

  @Autowired
  private PersistentUUIDTokenGenerator tokenGenerator;

  @BeforeClass
  public static void init() {
    TestUtils.initRestAssured();
  }

  @Test
  public void testResetPassword() {
    // String username = "test_user";
    //
    // RegistrationRequestDto reg = RegistrationUtils.createRegistrationRequest(username);
    // String confirmationKey = tokenGenerator.getLastToken();
    // RegistrationUtils.confirmRegistrationRequest(confirmationKey);
    // RegistrationUtils.approveRequest(reg.getUuid());
    // String resetKey = tokenGenerator.getLastToken();
    //
//    // @formatter:off
//    RestAssured.given()
//      .port(8080)
//      .pathParam("token", resetKey)
//    .when()
//      .get("/iam/password-reset/{token}")
//    .then()
//      .log()
//        .body(true)
//      .statusCode(HttpStatus.OK.value())
//    ;
//    // @formatter:on
    //
    // RegistrationUtils.deleteUser(reg.getAccountId());
  }

  @Test
  public void testResetPasswordWithInvalidResetKey() {
    // String username = "test_user";
    //
    // RegistrationRequestDto reg = RegistrationUtils.createRegistrationRequest(username);
    // String confirmationKey = tokenGenerator.getLastToken();
    // RegistrationUtils.confirmRegistrationRequest(confirmationKey);
    // RegistrationUtils.approveRequest(reg.getUuid());
    //
    // String resetKey = "abcdefghilmnopqrstuvz";
    //
//    // @formatter:off
//    RestAssured.given()
//      .port(8080)
//      .pathParam("token", resetKey)
//    .when()
//      .get("/iam/password-reset/{token}")
//    .then()
//      .log()
//        .body(true)
//      .statusCode(HttpStatus.OK.value())
//    ;
//    // @formatter:on
    //
    // RegistrationUtils.deleteUser(reg.getAccountId());
  }

  @Test
  public void testForgotPasswordWithAccountNotRegistered() {

    // String emailAddress = "test@foo.bar";
    //
//    // @formatter:off
//    RestAssured.given()
//      .port(8080)
//      .pathParam("email", emailAddress)
//    .when()
//      .get("/iam/password-forgot/{email}", emailAddress)
//    .then()
//      .log()
//        .body(true)
//      .statusCode(HttpStatus.NOT_FOUND.value())
//      .body("detail", Matchers.stringContainsInOrder(Arrays.asList("No account found for the email address")))
//    ;
//    // @formatter:on
  }



}
