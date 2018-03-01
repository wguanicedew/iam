package it.infn.mw.iam.notification;

public interface NotificationStoreService {

  public void clearExpiredNotifications();

  public int countPendingNotifications();

  public void clearAllNotifications();
}
