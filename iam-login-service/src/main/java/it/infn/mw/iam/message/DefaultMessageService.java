package it.infn.mw.iam.message;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.core.IamMessageStatus;
import it.infn.mw.iam.core.IamMessageType;
import it.infn.mw.iam.persistence.model.IamMessage;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

@Service
public class DefaultMessageService implements MessageService {

  @Override
  public IamMessage createMessage(final IamRegistrationRequest request,
      final IamMessageType messageType) {
    IamMessage message = new IamMessage();
    message.setUuid(UUID.randomUUID().toString());
    message.setCreationTime(new Date());
    message.setMessageType(messageType);
    message.setRequest(request);
    message.setMessageStatus(IamMessageStatus.PENDING);

    return message;
  }

  public void sendPendingMessages() {

  }

}
