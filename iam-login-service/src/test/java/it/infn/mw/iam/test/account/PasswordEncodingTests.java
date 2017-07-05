package it.infn.mw.iam.test.account;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.registration.PersistentUUIDTokenGenerator;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.RegistrationUtils;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class PasswordEncodingTests {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private PersistentUUIDTokenGenerator tokenGenerator;

  @Autowired
  private IamAccountRepository iamAccountRepository;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  private RegistrationRequestDto reg;

  @BeforeClass
  public static void init() {
    TestUtils.initRestAssured();
  }

  @After
  public void tearDown() {
    notificationRepository.deleteAll();
  }

  @Test
  public void testPasswordEncoded() {
    String username = "test_user";
    String newPassword = "secure_password";

    reg = RegistrationUtils.createRegistrationRequest(username);
    String confirmationKey = tokenGenerator.getLastToken();
    RegistrationUtils.confirmRegistrationRequest(confirmationKey);
    RegistrationUtils.approveRequest(reg.getUuid());

    String resetKey = tokenGenerator.getLastToken();

    RegistrationUtils.changePassword(resetKey, newPassword);

    IamAccount account = iamAccountRepository.findByUuid(reg.getAccountId()).get();

    Assert.assertTrue(passwordEncoder.matches(newPassword, account.getPassword()));

    RegistrationUtils.deleteUser(reg.getAccountId());
  }

}
