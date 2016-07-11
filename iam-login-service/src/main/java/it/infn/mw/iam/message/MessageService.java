package it.infn.mw.iam.message;

import it.infn.mw.iam.core.IamMessageType;
import it.infn.mw.iam.persistence.model.IamMessage;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface MessageService {

  public IamMessage createMessage(IamRegistrationRequest request, IamMessageType messageType);

}
