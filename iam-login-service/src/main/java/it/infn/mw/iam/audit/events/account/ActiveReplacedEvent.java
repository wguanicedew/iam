package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_ACTIVE;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class ActiveReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 5681737929767602266L;

  private final Boolean active;

  public ActiveReplacedEvent(Object source, IamAccount account, Boolean active) {
    super(source, account, ACCOUNT_REPLACE_ACTIVE, buildMessage(ACCOUNT_REPLACE_ACTIVE, active));
    this.active = active;
  }

  public Boolean getActive() {
    return active;
  }

  protected static String buildMessage(UpdaterType t, Boolean active) {
    return String.format("%s: %s", t.getDescription(), active);
  }
}
