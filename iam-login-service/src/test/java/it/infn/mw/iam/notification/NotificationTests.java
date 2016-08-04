package it.infn.mw.iam.notification;

import static it.infn.mw.iam.test.RegistrationUtils.createRegistrationRequest;
import static it.infn.mw.iam.test.RegistrationUtils.deleteUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.registration.RegistrationRequestDto;
import it.infn.mw.iam.test.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IamLoginService.class)
@WebIntegrationTest
public class NotificationTests {

  @Autowired
  @Qualifier("defaultNotificationService")
  private NotificationService notificationService;

  @Value("${notification.subject.confirmation}")
  private String subjectConfirm;

  @Value("${notification.mailFrom}")
  private String mailFrom;

  @Value("${notification.cleanupAge}")
  private Integer notificationCleanUpAge;

  @Value("${spring.mail.host}")
  private String mailHost;

  @Value("${spring.mail.port}")
  private Integer mailPort;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  private Wiser wiser;


  @BeforeClass
  public static void init() {

    TestUtils.initRestAssured();
  }

  @Before
  public void setUp() {
    wiser = new Wiser();
    wiser.setHostname(mailHost);
    wiser.setPort(mailPort);
    wiser.start();
  }

  @After
  public void tearDown() {
    wiser.stop();
  }

  @Test
  public void testSendEmails() throws MessagingException {

    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    notificationService.sendPendingNotification();

    Assert.assertTrue("element count", wiser.getMessages().size() == 1);
    WiserMessage message = wiser.getMessages().get(0);

    Assert.assertTrue("sender", mailFrom.equals(message.getEnvelopeSender()));
    Assert.assertTrue("receiver", message.getEnvelopeReceiver().startsWith(username));
    Assert.assertTrue("subject", subjectConfirm.equals(message.getMimeMessage().getSubject()));

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      Assert.assertTrue("status", IamDeliveryStatus.DELIVERED.equals(elem.getDeliveryStatus()));
    }

    deleteUser(reg.getAccountId());
  }

  @Test
  public void testSendMultipleNotifications() {

    int count = 3;
    List<RegistrationRequestDto> requestList = new ArrayList<>();

    for (int idx = 1; idx <= count; idx++) {
      RegistrationRequestDto reg = createRegistrationRequest("test_user_" + idx);
      requestList.add(reg);
    }

    notificationService.sendPendingNotification();

    Assert.assertTrue("element count", wiser.getMessages().size() == count);
    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      Assert.assertTrue("status", IamDeliveryStatus.DELIVERED.equals(elem.getDeliveryStatus()));
    }

    for (RegistrationRequestDto elem : requestList) {
      deleteUser(elem.getAccountId());
    }
  }

  @Test
  public void testSendWithEmptyQueue() {

    notificationService.sendPendingNotification();
    Assert.assertTrue("element count", wiser.getMessages().size() == 0);
  }

  @Test
  public void testDeliveryFailure() {
    String username = "test_user";
    RegistrationRequestDto reg = createRegistrationRequest(username);

    wiser.stop();

    notificationService.sendPendingNotification();

    Iterable<IamEmailNotification> queue = notificationRepository.findAll();
    for (IamEmailNotification elem : queue) {
      Assert.assertTrue("status",
          IamDeliveryStatus.DELIVERY_ERROR.equals(elem.getDeliveryStatus()));
    }

    deleteUser(reg.getAccountId());
  }

  @Test
  public void testCleanOldMessages() {
    String username = "test_user";

    RegistrationRequestDto reg = createRegistrationRequest(username);
    notificationService.sendPendingNotification();
    Assert.assertTrue("element count", wiser.getMessages().size() == 1);
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
