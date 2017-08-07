package it.infn.mw.iam.test.ext_authn.saml.jit_account_provisioning;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.infn.mw.iam.audit.events.account.AccountCreatedEvent;

@Configuration
public class JitTestConfig {

  public static class CountAccountCreatedEventsListener
      implements ApplicationListener<AccountCreatedEvent> {

    long eventCount = 0;

    @Override
    public void onApplicationEvent(AccountCreatedEvent event) {
      eventCount++;
    }

    public void resetCount() {
      eventCount = 0;
    }

    public long getCount() {
      return eventCount;
    }

  }

  public JitTestConfig() {}

  @Bean
  public CountAccountCreatedEventsListener accountCreatedEventiListener() {
    return new CountAccountCreatedEventsListener();
  }

}
