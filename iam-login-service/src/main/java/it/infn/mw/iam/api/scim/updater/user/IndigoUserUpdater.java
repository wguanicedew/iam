package it.infn.mw.iam.api.scim.updater.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class IndigoUserUpdater implements Updater<IamAccount, ScimUser> {

  private UserUpdaterCollection addUpdaters;
  private UserUpdaterCollection replaceUpdaters;
  private UserUpdaterCollection removeUpdaters;

  @Autowired
  public IndigoUserUpdater(OpenIDConnectAccountUpdater openIDConnectAccountUpdater,
      SshKeyUpdater sshKeyUpdater, SamlAccountUpdater samlAccountUpdater) {
    
    addUpdaters = new UserUpdaterCollection();
    addUpdaters.addUpdater(openIDConnectAccountUpdater);
    addUpdaters.addUpdater(sshKeyUpdater);
    addUpdaters.addUpdater(samlAccountUpdater);

    replaceUpdaters = new UserUpdaterCollection();
    replaceUpdaters.addUpdater(sshKeyUpdater);

    removeUpdaters = new UserUpdaterCollection();
    removeUpdaters.addUpdater(openIDConnectAccountUpdater);
    removeUpdaters.addUpdater(sshKeyUpdater);
    removeUpdaters.addUpdater(samlAccountUpdater);
  }

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getIndigoUser());
    Preconditions.checkArgument(!user.getIndigoUser().isEmpty());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    validate(account, user);

    return addUpdaters.add(account,  user);
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    validate(account, user);

    return removeUpdaters.remove(account,  user);
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    return replaceUpdaters.replace(account,  user);
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getIndigoUser() != null && !user.getIndigoUser().isEmpty();
  }

}
