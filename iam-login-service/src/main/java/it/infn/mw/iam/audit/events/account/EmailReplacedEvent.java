package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_EMAIL;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class EmailReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -2527611317336416111L;

  private final String email;

  public EmailReplacedEvent(Object source, IamAccount account, String email) {
    super(source, account, ACCOUNT_REPLACE_EMAIL, buildMessage(ACCOUNT_REPLACE_EMAIL, email));
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  protected static String buildMessage(UpdaterType t, String email) {
    return String.format("%s: %s", t.getDescription(), email);
  }

}
