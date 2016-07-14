package it.infn.mw.iam.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.core.IamNotificationType;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamNotificationReceiver;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;

@Service
public class DefaultMessageService implements MessageService {

  @Autowired
  private VelocityEngine velocityEngine;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Value("${notification.confirmation.subject}")
  private String confirmSubject;

  @Override
  public IamEmailNotification createConfirmationMessage(final IamRegistrationRequest request) {

    String recipient = request.getAccount().getUserInfo().getName();
    String confirmURL = String.format("%s/registration/confirm/%s", baseUrl,
        request.getAccount().getConfirmationKey());

    Map<String, Object> model = new HashMap<>();
    model.put("recipient", recipient);
    model.put("confirmURL", confirmURL);

    String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
        "confirmRegistration.vm", "UTF-8", model);

    List<IamNotificationReceiver> receivers = new ArrayList<>();
    IamNotificationReceiver rcv = new IamNotificationReceiver();
    rcv.setAccount(request.getAccount());
    receivers.add(rcv);

    IamEmailNotification message = new IamEmailNotification();
    message.setUuid(UUID.randomUUID().toString());
    message.setType(IamNotificationType.CONFIRMATION);
    message.setSubject(confirmSubject);
    message.setBody(body);
    message.setCreationTime(new Date());
    message.setDeliveryStatus(IamDeliveryStatus.PENDING);
    message.setReceivers(receivers);

    notificationRepository.save(message);

    return message;
  }

  public void sendPendingMessages() {

  }

}
