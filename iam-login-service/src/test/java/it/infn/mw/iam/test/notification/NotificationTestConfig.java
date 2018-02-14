package it.infn.mw.iam.test.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import it.infn.mw.iam.notification.NotificationProperties;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
import it.infn.mw.iam.test.util.MockTimeProvider;
import it.infn.mw.iam.test.util.notification.MockNotificationDelivery;

@Configuration
public class NotificationTestConfig {

  @Bean
  public MockTimeProvider timeProvider() {
    return new MockTimeProvider();
  }

  @Bean
  @Primary
  public MockNotificationDelivery notificationDelivery(IamEmailNotificationRepository repo,
      NotificationProperties properties) {
    return new MockNotificationDelivery(repo, properties, timeProvider());
  }

}
