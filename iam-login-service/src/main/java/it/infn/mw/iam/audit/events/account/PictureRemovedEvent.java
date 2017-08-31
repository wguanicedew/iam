package it.infn.mw.iam.audit.events.account;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_PICTURE;

import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.persistence.model.IamAccount;

public class PictureRemovedEvent extends AccountUpdatedEvent {

  private static final long serialVersionUID = 4301089543652866437L;

  private final String picture;

  public PictureRemovedEvent(Object source, IamAccount account, String picture) {
    super(source, account, ACCOUNT_REMOVE_PICTURE, buildMessage(ACCOUNT_REMOVE_PICTURE, picture));
    this.picture = picture;
  }

  public String getPictures() {
    return picture;
  }

  protected static String buildMessage(UpdaterType t, String picture) {
    return String.format("%s: %s", t.getDescription(), picture);
  }
}
