package it.infn.mw.iam.api.scim.updater.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class NameUpdater implements Updater<IamAccount, ScimUser> {

  private UserUpdaterCollection updaters;

  @Autowired
  public NameUpdater(GivenNameUpdater givenNameUpdater, MiddleNameUpdater middleNameUpdater,
      FamilyNameUpdater familyNameUpdater) {
    
    updaters = new UserUpdaterCollection();
    updaters.addUpdater(givenNameUpdater);
    updaters.addUpdater(middleNameUpdater);
    updaters.addUpdater(familyNameUpdater);
  }

  private void validate(IamAccount account, ScimUser user) {
    
    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    validate(account, user);
    return updaters.add(account, user);
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Remove Name not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);
    return updaters.replace(account, user);
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getName() != null;
  }

}
