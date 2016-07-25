package it.infn.mw.iam.notification;

import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface NotificationService {

  public IamEmailNotification createConfirmationMessage(IamRegistrationRequest request);

  public void sendPendingNotification();

  public void clearExpiredNotifications();

}
