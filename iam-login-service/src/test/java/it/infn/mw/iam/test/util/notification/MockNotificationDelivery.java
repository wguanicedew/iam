package it.infn.mw.iam.test.util.notification;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.core.time.TimeProvider;
import it.infn.mw.iam.notification.LoggingNotificationDelivery;
import it.infn.mw.iam.notification.NotificationProperties;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;

public class MockNotificationDelivery extends LoggingNotificationDelivery {

  List<IamEmailNotification> deliveredNotifications = new LinkedList<>();

  public MockNotificationDelivery(IamEmailNotificationRepository repo,
      NotificationProperties properties, TimeProvider provider) {
    super(repo, properties, provider);
  }

  protected void deliverPendingNotification(IamEmailNotification e) {
    e.setDeliveryStatus(IamDeliveryStatus.DELIVERED);
    e.setLastUpdate(new Date(timeProvider.currentTimeMillis()));
    repo.save(e);
    deliveredNotifications.add(e);
  }

  @Override
  public void sendPendingNotifications() {
    if (!properties.getDisable()) {
      repo.findByDeliveryStatus(IamDeliveryStatus.PENDING)
        .forEach(this::deliverPendingNotification);
    }
  }

  public List<IamEmailNotification> getDeliveredNotifications() {
    return deliveredNotifications;
  }

  public void clearDeliveredNotifications() {
    deliveredNotifications.clear();
  }
}
