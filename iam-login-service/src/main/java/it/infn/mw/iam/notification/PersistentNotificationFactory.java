package it.infn.mw.iam.notification;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.core.IamNotificationType;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.repository.IamEmailNotificationRepository;
@Service
public class PersistentNotificationFactory extends TransientNotificationFactory {

  final IamEmailNotificationRepository repo;
  
  @Autowired
  public PersistentNotificationFactory(VelocityEngine ve, NotificationProperties np, 
      IamEmailNotificationRepository repo) {
    super(ve, np);
    this.repo = repo; 
  }
  
  @Override
  protected IamEmailNotification createMessage(String template, Map<String, Object> model,
      IamNotificationType messageType, String subject, String receiverAddress) {
    
    IamEmailNotification message = super.createMessage(template, model, messageType, subject, 
        receiverAddress); 
    
    return repo.save(message); 
  }

}
