package it.infn.mw.iam.notification;

import static it.infn.mw.iam.test.RegistrationUtils.createRegistrationRequest;
import static it.infn.mw.iam.test.RegistrationUtils.deleteUser;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class NotificationTest {

  @Autowired
  @Qualifier("fakeNotificationService")
  private FakeNotificationService notificationService;

  @Value("${notification.subject.confirmation}")
  private String subjectConfirm;

  @Value("${notification.mailFrom}")
  private String mailFrom;

  @Value("${notification.cleanupAge}")
  private Integer notificationCleanUpAge;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;


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
    Assert.assertTrue("subject", subjectConfirm.equals(elem.getSubject()));
    Assert.assertTrue("receiver count", elem.getTo().length == 1);
    Assert.assertTrue("receiver", elem.getTo()[0].startsWith(username));

    deleteUser(reg.getAccountId());
  }

  @Test
  public void testCleanOldMessages() {
    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);

    notificationService.getSendedNotification();

    agingMessages();

    notificationService.clearExpiredNotifications();

    Integer count = notificationRepository.countAllMessages();
    Assert.assertTrue("messages count", count == 0);

    deleteUser(reg.getAccountId());
  }

  private void agingMessages() {
    Date fakeDate = DateUtils.addDays(new Date(), -(notificationCleanUpAge + 1));

    Iterable<IamEmailNotification> iter =
        notificationRepository.findAll(new Sort(Sort.Direction.ASC, "id"));
    for (IamEmailNotification elem : iter) {
      elem.setLastUpdate(fakeDate);
    }
    notificationRepository.save(iter);
  }

}
