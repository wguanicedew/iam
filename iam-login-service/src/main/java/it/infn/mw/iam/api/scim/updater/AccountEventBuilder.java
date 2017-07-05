package it.infn.mw.iam.api.scim.updater;

import it.infn.mw.iam.audit.events.account.AccountEvent;
import it.infn.mw.iam.persistence.model.IamAccount;

@FunctionalInterface
public interface AccountEventBuilder<T,E extends AccountEvent> {

  E buildEvent(Object source, IamAccount account, T newValue);

}
