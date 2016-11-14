package it.infn.mw.iam.api.scim.updater.user;

import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class ActiveUpdater implements Updater<IamAccount, ScimUser> {

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getActive());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    return replace(account, user);
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {
    throw new ScimPatchOperationNotSupported("Remove active is not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    if (account.isActive() ^ user.getActive()) {
      account.setActive(user.getActive());
      return true;
    }
    return false;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getActive() != null;
  }

}
