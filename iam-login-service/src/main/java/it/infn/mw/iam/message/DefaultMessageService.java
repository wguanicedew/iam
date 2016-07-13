package it.infn.mw.iam.message;

import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.core.IamNotificationType;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

@Service
public class DefaultMessageService implements MessageService {

  @Override
  public IamEmailNotification createMessage(final IamRegistrationRequest request,
      final IamNotificationType messageType) {
    IamEmailNotification message = new IamEmailNotification();
    message.setUuid(UUID.randomUUID().toString());
    message.setCreationTime(new Date());

    return message;
  }

  public void sendPendingMessages() {

  }

}
