package it.infn.mw.iam.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import it.infn.mw.iam.core.IamDeliveryStatus;
import it.infn.mw.iam.core.IamNotificationType;
import it.infn.mw.iam.core.time.TimeProvider;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamNotificationReceiver;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;

@Service
@Qualifier("defaultNotificationService")
public class DefaultNotificationService implements NotificationService {

  public static final Logger logger = LoggerFactory.getLogger(DefaultNotificationService.class);

  @Autowired
  private VelocityEngine velocityEngine;

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private IamEmailNotificationRepository notificationRepository;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Value("${notification.subject.confirmation}")
  private String subjectConfirm;

  @Value("${notification.subject.activated}")
  private String subjectActivated;

  @Value("${notification.mailFrom}")
  private String mailFrom;

  @Value("${notification.cleanupAge}")
  private Integer notificationCleanUpAge;

  @Autowired
  private TimeProvider timeProvider;


  @Override
  public IamEmailNotification createConfirmationMessage(final IamRegistrationRequest request) {

    String recipient = request.getAccount().getUserInfo().getName();
    String confirmURL = String.format("%s/registration/verify/%s", baseUrl,
        request.getAccount().getConfirmationKey());

    Map<String, Object> model = new HashMap<>();
    model.put("recipient", recipient);
    model.put("confirmURL", confirmURL);

    return createMessage("confirmRegistration.vm", model, IamNotificationType.CONFIRMATION,
        subjectConfirm, request);
  }

  @Override
  public IamEmailNotification createAccountActivatedMessage(final IamRegistrationRequest request) {

    String recipient = request.getAccount().getUserInfo().getName();
    String username = request.getAccount().getUsername();
    String password = request.getAccount().getPassword();

    Map<String, Object> model = new HashMap<>();
    model.put("recipient", recipient);
    model.put("username", username);
    model.put("password", password);

    return createMessage("accountActivated.vm", model, IamNotificationType.ACTIVATED,
        subjectActivated, request);
  }

  @Override
  @Transactional
  public void sendPendingNotification() {

    SimpleMailMessage messageTemplate = new SimpleMailMessage();
    messageTemplate.setFrom(mailFrom);

    List<IamEmailNotification> messageQueue =
        notificationRepository.findByDeliveryStatus(IamDeliveryStatus.PENDING);

    for (IamEmailNotification elem : messageQueue) {
      messageTemplate.setSubject(elem.getSubject());
      messageTemplate.setText(elem.getBody());

      for (IamNotificationReceiver receiver : elem.getReceivers()) {
        messageTemplate.setTo(receiver.getAccount().getUserInfo().getEmail());
      }

      try {
        doSend(messageTemplate);

        elem.setDeliveryStatus(IamDeliveryStatus.DELIVERED);

        logger.info("Sent mail. message_id:{} status:{} message_type:{} mail_from:{} rcpt_to:{}",
            elem.getUuid(), elem.getDeliveryStatus().name(), elem.getType(), mailFrom,
            messageTemplate.getTo());

      } catch (MailException me) {
        elem.setDeliveryStatus(IamDeliveryStatus.DELIVERY_ERROR);
        logger.error("Message delivery fail. message_id:{} reason:{}", elem.getUuid(),
            me.getMessage());
      }

      elem.setLastUpdate(new Date());
      notificationRepository.save(elem);
    }
  }

  @Override
  public void clearExpiredNotifications() {

    Date currentTime = new Date(timeProvider.currentTimeMillis());
    Date threshold = DateUtils.addDays(currentTime, -notificationCleanUpAge);

    List<IamEmailNotification> messageList =
        notificationRepository.findByStatusWithUpdateTime(IamDeliveryStatus.DELIVERED, threshold);

    if (!messageList.isEmpty()) {
      notificationRepository.delete(messageList);
      logger.info("Deleted {} messages in status {} older than {}", messageList.size(),
          IamDeliveryStatus.DELIVERED, threshold);
    }
  }


  protected void doSend(final SimpleMailMessage message) {
    mailSender.send(message);
  }


  private IamEmailNotification createMessage(final String template, final Map<String, Object> model,
      final IamNotificationType messageType, final String subject,
      final IamRegistrationRequest request) {

    String body =
        VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, "UTF-8", model);

    IamEmailNotification message = new IamEmailNotification();
    message.setUuid(UUID.randomUUID().toString());
    message.setType(messageType);
    message.setSubject(subject);
    message.setBody(body);
    message.setCreationTime(new Date());
    message.setDeliveryStatus(IamDeliveryStatus.PENDING);
    message.setRequest(request);

    List<IamNotificationReceiver> receivers = new ArrayList<>();
    IamNotificationReceiver rcv = new IamNotificationReceiver();
    rcv.setIamEmailNotification(message);
    rcv.setAccount(request.getAccount());
    receivers.add(rcv);

    message.setReceivers(receivers);

    notificationRepository.save(message);

    return message;
  }

}
