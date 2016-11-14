package it.infn.mw.iam.api.scim.updater.user;
 
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class FamilyNameUpdater implements Updater<IamAccount, ScimUser> {

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getName());
    Preconditions.checkNotNull(user.getName().getFamilyName());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    return replace(account, user);
  }

  @Override
  public boolean remove(IamAccount accpunt, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Remove family name is not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    if (user.getName().getFamilyName().equals(account.getUserInfo().getFamilyName())) {
      return false;
    }
    account.getUserInfo().setFamilyName(user.getName().getFamilyName());
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getName() != null && user.getName().getFamilyName() != null;
  }
}
