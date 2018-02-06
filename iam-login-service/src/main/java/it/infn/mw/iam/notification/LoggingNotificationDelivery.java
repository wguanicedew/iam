package it.infn.mw.iam.notification;

import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.core.time.TimeProvider;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamNotificationReceiver;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;

@Service
@ConditionalOnProperty(name = "notification.disable", havingValue = "true")
public class LoggingNotificationDelivery implements NotificationDelivery {

  public static final Logger LOG = LoggerFactory.getLogger(LoggingNotificationDelivery.class);

  protected final IamEmailNotificationRepository repo;
  protected final NotificationProperties properties;
  protected final TimeProvider timeProvider;

  @Autowired
  public LoggingNotificationDelivery(IamEmailNotificationRepository repo,
      NotificationProperties properties, TimeProvider provider) {

    this.repo = repo;
    this.properties = properties;
    this.timeProvider = provider;
  }

  protected void logEmailNotificationAndSetDelivered(IamEmailNotification e) {
    String receivers =
        e.getReceivers().stream().map(IamNotificationReceiver::getEmailAddress).collect(
            Collectors.joining(","));

    LOG.info("Email message [To:'{}' Subject:'{}' Body:'{}']", receivers, e.getSubject(),
        e.getBody());
    e.setDeliveryStatus(IamDeliveryStatus.DELIVERED);
    e.setLastUpdate(new Date(timeProvider.currentTimeMillis()));
    repo.save(e);
  }

  @Override
  public void sendPendingNotifications() {
    repo.findByDeliveryStatus(IamDeliveryStatus.PENDING)
      .forEach(this::logEmailNotificationAndSetDelivered);
  }

}
