package it.infn.mw.iam.api.scim.updater;

import org.springframework.context.ApplicationEventPublisher;

@FunctionalInterface
public interface AccountEventPublisher {

  void publishAccountEvent(Object source, ApplicationEventPublisher publisher);
}
