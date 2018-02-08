package it.infn.mw.iam.notification;

@FunctionalInterface
public interface NotificationDelivery {

  public void sendPendingNotifications();
  
}
