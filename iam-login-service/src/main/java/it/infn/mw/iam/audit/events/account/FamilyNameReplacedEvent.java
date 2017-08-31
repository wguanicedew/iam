package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_FAMILY_NAME;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class FamilyNameReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 1984232242208443669L;

  private final String familyName;

  public FamilyNameReplacedEvent(Object source, IamAccount account, String familyName) {
    super(source, account, ACCOUNT_REPLACE_FAMILY_NAME,
        buildMessage(ACCOUNT_REPLACE_FAMILY_NAME, familyName));
    this.familyName = familyName;
  }

  public String getFamilyName() {
    return familyName;
  }

  protected static String buildMessage(UpdaterType t, String familyName) {
    return String.format("%s: %s", t.getDescription(), familyName);
  }
}
