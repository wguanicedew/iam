package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_GIVEN_NAME;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class GivenNameReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -8832461145651484401L;

  private final String givenName;

  public GivenNameReplacedEvent(Object source, IamAccount account, String givenName) {
    super(source, account, ACCOUNT_REPLACE_GIVEN_NAME,
        buildMessage(ACCOUNT_REPLACE_GIVEN_NAME, givenName));
    this.givenName = givenName;
  }

  public String getGivenName() {
    return givenName;
  }

  protected static String buildMessage(UpdaterType t, String givenName) {
    return String.format("%s: %s", t.getDescription(), givenName);
  }
}
