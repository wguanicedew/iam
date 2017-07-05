package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PICTURE;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class PictureReplacedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = -2527611317336416111L;

  private final String picture;

  public PictureReplacedEvent(Object source, IamAccount account, String picture) {
    super(source, account, ACCOUNT_REPLACE_PICTURE, buildMessage(ACCOUNT_REPLACE_PICTURE, picture));
    this.picture = picture;
  }

  public String getPicture() {
    return picture;
  }

  protected static String buildMessage(UpdaterType t, String picture) {
    return String.format("%s: %s", t.getDescription(), picture);
  }
}
