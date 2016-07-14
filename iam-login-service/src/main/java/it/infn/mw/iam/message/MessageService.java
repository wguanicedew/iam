package it.infn.mw.iam.message;

import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface MessageService {

  public IamEmailNotification createConfirmationMessage(IamRegistrationRequest request);

}
