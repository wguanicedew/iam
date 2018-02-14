package it.infn.mw.iam.test.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health.Builder;

import it.infn.mw.iam.actuator.endpoint.IamMailHealthIndicator;

public class MockMailHealthIndicator extends IamMailHealthIndicator {

  boolean active = false;
  String mailhost;
  int mailPort;

  @Autowired
  public MockMailHealthIndicator() {
    super(null);
  }


  public boolean isActive() {
    return active;
  }


  public void setActive(boolean active) {
    this.active = active;
  }



  public String getMailhost() {
    return mailhost;
  }


  public void setMailhost(String mailhost) {
    this.mailhost = mailhost;
  }


  public int getMailPort() {
    return mailPort;
  }


  public void setMailPort(int mailPort) {
    this.mailPort = mailPort;
  }


  @Override
  protected void doHealthCheck(Builder builder) throws Exception {

    builder.withDetail("location", String.format("%s:%d", mailhost, mailPort));
    
    if (isActive()) {
      builder.up();
    } else {
      builder.down();
    }
  }
}
