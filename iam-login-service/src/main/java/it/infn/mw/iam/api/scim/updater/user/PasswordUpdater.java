package it.infn.mw.iam.api.scim.updater.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;

@Component
public class PasswordUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private PasswordEncoder passwordEncoder;

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getPassword());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {
    
    validate(account, user);

    final String encodedPassword = passwordEncoder.encode(user.getPassword());
    if (account.getPassword().equals(encodedPassword)) {
      return false;
    }
    account.setPassword(encodedPassword);
    return true;
  }

  @Override
  public boolean remove(IamAccount accpunt, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Remove password is not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    return add(account, user);
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getPassword() != null;
  }

}
