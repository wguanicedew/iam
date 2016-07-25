package it.infn.mw.iam.notification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@Qualifier("fakeNotificationService")
public class FakeNotificationService extends DefaultNotificationService
    implements NotificationService {

  private List<SimpleMailMessage> messageList = new ArrayList<>();

  @Override
  public void doSend(final SimpleMailMessage message) {
    messageList.add(message);
  }

  public List<SimpleMailMessage> getSendedNotification() {
    super.sendPendingNotification();
    return messageList;
  }

}
