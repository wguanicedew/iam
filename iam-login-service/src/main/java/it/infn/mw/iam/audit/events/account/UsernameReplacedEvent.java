package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_USERNAME;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class UsernameReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -4559709075463515934L;

  private final String username;

  public UsernameReplacedEvent(Object source, IamAccount account, String username) {
    super(source, account, ACCOUNT_REPLACE_USERNAME,
        buildMessage(ACCOUNT_REPLACE_USERNAME, username));
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  protected static String buildMessage(UpdaterType t, String username) {
    return String.format("%s: %s", t.getDescription(), username);
  }
}
