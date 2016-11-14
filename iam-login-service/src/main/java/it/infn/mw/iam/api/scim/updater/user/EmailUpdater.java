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
public class EmailUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private IamAccountRepository accountRepository;

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getEmails());
    Preconditions.checkArgument(user.getEmails().size() == 1,
        "Specifying more than one email is not supported!");
    Preconditions.checkNotNull(user.getEmails().get(0), "Null email found");
    Preconditions.checkNotNull(user.getEmails().get(0).getValue(), "Null email value found");

    final String email = user.getEmails().get(0).getValue();
    if (accountRepository.findByEmailWithDifferentUUID(email, account.getUuid()).isPresent()) {
      throw new ScimResourceExistsException("email " + email + " already assigned to another user");
    }
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    return replace(account, user);
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Remove email is not supported");
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    validate(account, user);

    final String email = user.getEmails().get(0).getValue();

    if (email.equals(account.getUserInfo().getEmail())) {
      return false;
    }
    account.getUserInfo().setEmail(email);
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getEmails() != null && !user.getEmails().isEmpty();
  }

}
