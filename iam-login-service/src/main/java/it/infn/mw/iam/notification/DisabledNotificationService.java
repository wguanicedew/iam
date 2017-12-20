package it.infn.mw.iam.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "notification.disable", havingValue = "true")
@Qualifier("defaultNotificationService")
public class DisabledNotificationService extends DefaultNotificationService {
  public static final Logger LOG = LoggerFactory.getLogger(DisabledNotificationService.class);

  @Override
  public void sendPendingNotifications() {
    LOG.info("Notification service is disabled, pending notifications will not be sent.");
  }

  @Override
  public void clearExpiredNotifications() {
    LOG.info("Notification service is disabled, expired notifications will not be cleared.");
  }

  @Override
  public int countPendingNotifications() {
    LOG.info("Notification service is disabled, assuming 0 pending notifications.");
    return 0;
  }

  @Override
  public void clearAllNotifications() {
    LOG.info("Notification service is disabled, notifications will not be cleared.");
  }

}
