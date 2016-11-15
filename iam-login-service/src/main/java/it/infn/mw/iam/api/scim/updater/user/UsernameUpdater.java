package it.infn.mw.iam.api.scim.updater.user;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class UsernameUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private IamAccountRepository accountRepository;

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getUserName());

    final String username = user.getUserName();
    final String uuid = account.getUuid();

    if (accountRepository.findByUsernameWithDifferentUUID(username, uuid).isPresent()) {
      throw new ScimResourceExistsException(
          "username " + username + " already assigned to another user");
    }
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    return replace(account, user);
  }

  @Override
  public boolean remove(IamAccount accpunt, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Remove username is not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    if (user.getUserName().equals(account.getUsername())) {
      return false;
    }
    account.setUsername(user.getUserName());
    return true;
  }

  @Override
  public boolean accept(ScimUser updates) {

    return updates.getUserName() != null;
  }
}
