package it.infn.mw.iam.actuator.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.MailHealthIndicator;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class IamMailHealthIndicator extends MailHealthIndicator {

  @Autowired
  public IamMailHealthIndicator(JavaMailSenderImpl mailSender) {
    super(mailSender);
    
  }

}
