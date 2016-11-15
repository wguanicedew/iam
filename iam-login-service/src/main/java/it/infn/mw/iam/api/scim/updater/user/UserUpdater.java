package it.infn.mw.iam.api.scim.updater.user;

import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

public interface UserUpdater extends Updater<IamAccount, ScimUser> {

  boolean isInvolved(ScimUser updates);
}
