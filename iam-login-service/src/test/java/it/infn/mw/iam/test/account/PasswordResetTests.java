package it.infn.mw.iam.test.account;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.notification.NotificationService;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.RegistrationUtils;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class PasswordResetTests {

  @Value("${server.port}")
  private Integer iamPort;

  @Autowired
  private PersistentUUIDTokenGenerator tokenGenerator;

  @Autowired
  private NotificationService notificationService;

  private RegistrationRequestDto registrationRequest;

  @BeforeClass
  public static void init() {
    TestUtils.initRestAssured();
  }

  @Before
  public void setup() {
    String username = "test_user";

    registrationRequest = RegistrationUtils.createRegistrationRequest(username);
    String confirmationKey = tokenGenerator.getLastToken();
    RegistrationUtils.confirmRegistrationRequest(confirmationKey);
    RegistrationUtils.approveRequest(registrationRequest.getUuid());
  }

  @After
  public void tearDown() {
    RegistrationUtils.deleteUser(registrationRequest.getAccountId());
    notificationService.clearAllNotifications();
  }



  @Test
  public void testChangePassword() {
    String newPassword = "secure_password";

    // @formatter:off
    RestAssured.given()
      .port(iamPort)
      .formParam("email", "test_user@example.org")
    .when()
      .post("/iam/password-reset/token")
    .then()
      .log()
        .all(true)
      .statusCode(HttpStatus.OK.value());
    // @formatter:on

    String resetToken = tokenGenerator.getLastToken();


    // Check token is valid

    // @formatter:off
    RestAssured.given()
      .log().all()
      .port(iamPort)
      .pathParam("token", resetToken)
    .when()
      .head("/iam/password-reset/token/{token}")
    .then()
      .log()
        .all(true)
      .statusCode(HttpStatus.OK.value());
    // @formatter:on


    // Reset password

    // @formatter:off
    RestAssured.given()
      .port(iamPort)
      .log().all()
      .param("token", resetToken)
      .param("password", newPassword)
    .when()
      .post("/iam/password-reset")
    .then()
      .log()
        .all()
      .statusCode(HttpStatus.OK.value());
    // @formatter:on


    // Check token is invalid

    // @formatter:off
    RestAssured.given()
      .port(iamPort)
      .log().all()
      .pathParam("token", resetToken)
    .when()
      .head("/iam/password-reset/token/{token}")
    .then()
      .log()
        .all()
      .statusCode(HttpStatus.NOT_FOUND.value());
    // @formatter:on

  }

  @Test
  public void testResetPasswordWithInvalidResetToken() {

    String resetToken = "abcdefghilmnopqrstuvz";

    // @formatter:off
    RestAssured.given()
      .port(iamPort)
      .log().all()
      .pathParam("token", resetToken)
    .when()
      .head("/iam/password-reset/token/{token}")
    .then()
      .log()
        .body(true)
      .statusCode(HttpStatus.NOT_FOUND.value())
    ;
    // @formatter:on
  }

  @Test
  public void testCreatePasswordResetTokenReturnsOkForUnknownAddress() {

    String emailAddress = "test@foo.bar";

    // This is needed to "forget" the token generated for the user
    // created by the setup method (which is not used by this test)
    String lastToken = tokenGenerator.getLastToken();

    // @formatter:off
    RestAssured.given()
      .log().all()
      .port(iamPort)
      .formParam("email", emailAddress)
    .when()
      .post("/iam/password-reset/token")
    .then()
      .log()
        .all()
      .statusCode(HttpStatus.OK.value())
    ;
    // @formatter:on


    // This checks that the token generator has not been called
    Assert.assertThat(tokenGenerator.getLastToken(), Matchers.equalTo(lastToken));

  }

  @Test
  public void testEmailValidationForPasswordResetTokenCreation() {
    String invalidEmailAddress = "this_is_not_an_email";

    // @formatter:off
    RestAssured.given()
      .log().all()
      .port(iamPort)
      .formParam("email", invalidEmailAddress)
    .when()
      .post("/iam/password-reset/token")
    .then()
      .log()
        .all()
      .statusCode(HttpStatus.BAD_REQUEST.value())
      .body(Matchers.equalTo("validation error: please specify a valid email address"));
    ; 
    
    // @formatter:off
    RestAssured.given()
      .log().all()
      .port(iamPort)
    .when()
      .post("/iam/password-reset/token")
    .then()
      .log()
        .all()
      .statusCode(HttpStatus.BAD_REQUEST.value())
      .body(Matchers.equalTo("validation error: please specify an email address"));
    ; 
  }

}
