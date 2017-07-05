package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PASSWORD;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class PasswordReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -1656076046003614399L;

  private final String password;

  public PasswordReplacedEvent(Object source, IamAccount account, String password) {
    super(source, account, ACCOUNT_REPLACE_PASSWORD,
        buildMessage(ACCOUNT_REPLACE_PASSWORD, password));
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  protected static String buildMessage(UpdaterType t, String password) {
    return String.format("%s: %s", t.getDescription(), password);
  }
}
