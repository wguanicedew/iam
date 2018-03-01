package it.infn.mw.iam.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NotificationDeliveryTask implements Runnable {

  final NotificationDelivery delivery;
  
  @Autowired
  public NotificationDeliveryTask(NotificationDelivery nd) {
    this.delivery = nd;
  }

  @Override
  @Transactional
  public void run() {
    delivery.sendPendingNotifications();
  }

}
