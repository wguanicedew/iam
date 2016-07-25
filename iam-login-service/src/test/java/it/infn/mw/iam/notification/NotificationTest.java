package it.infn.mw.iam.notification;

import static it.infn.mw.iam.test.RegistrationUtils.createRegistrationRequest;
import static it.infn.mw.iam.test.RegistrationUtils.deleteUser;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class NotificationTest {

  @Autowired
  @Qualifier("fakeNotificationService")
  private FakeNotificationService notificationService;

  @Value("${notification.confirmation.subject}")
  private String confirmSubject;

  @Value("${notification.mailFrom}")
  private String mailFrom;

  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Test
  public void testSendConfirmation() {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);

    List<SimpleMailMessage> messageList = notificationService.getSendedNotification();
    Assert.assertTrue("element count", messageList.size() == 1);

    SimpleMailMessage elem = messageList.get(0);

    Assert.assertTrue("sender", mailFrom.equals(elem.getFrom()));
    Assert.assertTrue("subject", confirmSubject.equals(elem.getSubject()));
    Assert.assertTrue("receiver count", elem.getTo().length == 1);
    Assert.assertTrue("receiver", elem.getTo()[0].startsWith(username));

    deleteUser(reg.getAccountId());
  }

  @Test
  public void testCleanOldMessages() {
    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);

  }

}
