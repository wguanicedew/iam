package it.infn.mw.iam.message;

import it.infn.mw.iam.core.IamNotificationType;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface MessageService {

  public IamEmailNotification createMessage(IamRegistrationRequest request, IamNotificationType messageType);

}
