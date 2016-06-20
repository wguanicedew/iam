package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;

@Component
public class UserUpdater implements Updater<IamAccount, ScimUser> {

  private final IamAccountRepository accountRepository;
  private final IamOidcIdRepository oidcIdRepository;
  private final OidcIdConverter oidcIdConverter;
  private final AddressConverter addressConverter;

  @Autowired
  public UserUpdater(IamAccountRepository accountRepository, IamOidcIdRepository oidcIdRepository,
      OidcIdConverter oidcIdConverter, AddressConverter addressConverter) {

    this.accountRepository = accountRepository;
    this.oidcIdRepository = oidcIdRepository;
    this.oidcIdConverter = oidcIdConverter;
    this.addressConverter = addressConverter;
  }

  public void update(IamAccount account, List<ScimPatchOperation<ScimUser>> operations) {

    for (ScimPatchOperation<ScimUser> op : operations) {

      switch (op.getOp()) {

        case add:
          addNotNullInfo(account, op.getValue());
          break;
        case remove:
          removeNotNullInfo(account, op.getValue());
          break;
        case replace:
          replaceNotNullInfo(account, op.getValue());
          break;
        default:
          break;
      }
    }
    return;
  }

  private void addNotNullInfo(IamAccount a, ScimUser u) {

    updateUserNameIfNotNull(a, u.getUserName());
    updateActiveIfNotNull(a, u.getActive());
    updateNameIfNotNull(a, u.getName());
    updateEmailIfNotNull(a, u.getEmails());
    updateAddressIfNotNull(a, u.getAddresses());
    updatePasswordIfNotNull(a, u.getPassword());

    if (u.getIndigoUser() != null) {

      addOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }
    
    accountRepository.save(a);
  }

  private void updatePasswordIfNotNull(IamAccount a, String password){
    if (password != null) {
      a.setPassword(password);
    }
  }
  private void updateAddressIfNotNull(IamAccount a, List<ScimAddress> addresses) {

    if (addresses != null && !addresses.isEmpty()) {

      a.getUserInfo().setAddress(addressConverter.fromScim(addresses.get(0)));
      a.touch();
    }
  }

  private void updateActiveIfNotNull(IamAccount a, Boolean active) {

    if (active != null) {
      if (a.isActive() ^ active) {

        a.setActive(active);
        a.touch();
      }
    }
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    if (u.getIndigoUser() != null) {

      removeOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }
  }

  private void replaceNotNullInfo(IamAccount a, ScimUser u) {

    updateUserNameIfNotNull(a, u.getUserName());
    updateActiveIfNotNull(a, u.getActive());
    updateNameIfNotNull(a, u.getName());
    updateEmailIfNotNull(a, u.getEmails());
    updateAddressIfNotNull(a, u.getAddresses());
  }

  private void updateUserNameIfNotNull(IamAccount a, String userName) {

    if (userName != null) {

      a.setUsername(userName);
      a.touch();
    }
  }

  private void updateNameIfNotNull(IamAccount a, ScimName name) {

    if (name != null) {

      a.getUserInfo().setFamilyName(
          name.getFamilyName() != null ? name.getFamilyName() : a.getUserInfo().getFamilyName());
      a.getUserInfo().setGivenName(
          name.getGivenName() != null ? name.getGivenName() : a.getUserInfo().getGivenName());
      a.getUserInfo().setMiddleName(
          name.getMiddleName() != null ? name.getMiddleName() : a.getUserInfo().getGivenName());
      a.getUserInfo()
          .setName(name.getFormatted() != null ? name.getFormatted() : a.getUserInfo().getName());
      a.touch();
      
    }
  }

  private void updateEmailIfNotNull(IamAccount a, List<ScimEmail> emails) {

    if (emails != null && !emails.isEmpty()) {

      a.getUserInfo().setEmail(emails.get(0).getValue());
      a.touch();
    }
  }

  private void addOidcIdsIfNotNull(IamAccount a, List<ScimOidcId> oidcIds) {

    if (oidcIds != null) {

      for (ScimOidcId oidc : oidcIds) {

        addOidcIdIfNotNull(a, oidc);
      }
    }
  }

  private void addOidcIdIfNotNull(IamAccount a, ScimOidcId oidcId) {

    if (oidcId != null) {

      IamOidcId current = oidcIdConverter.fromScim(oidcId);

      if (current.getAccount() == null) {

        current.setAccount(a);
        oidcIdRepository.save(current);

        a.getOidcIds().add(current);
        a.touch();
        accountRepository.save(a);

      } else if (current.getAccount().getUuid() != a.getUuid()) {

        throw new ScimResourceExistsException("Cannot add OpenID Connect account to user "
            + a.getUsername() + " because it's already associated to another user");
      }
    }
  }

  private void removeOidcIdsIfNotNull(IamAccount a, List<ScimOidcId> oidcIds) {

    if (oidcIds != null) {

      for (ScimOidcId oidc : oidcIds) {

        removeOidcIdIfNotNull(a, oidc);
      }
    }
  }

  private void removeOidcIdIfNotNull(IamAccount a, ScimOidcId oidcId) {

    if (oidcId != null) {

      IamOidcId current = oidcIdConverter.fromScim(oidcId);

      if (current.getAccount() != null && current.getAccount().getUuid() == a.getUuid()) {

        a.getOidcIds().remove(current);
        a.touch();
        accountRepository.save(a);

        oidcIdRepository.delete(current);

      } else {
        throw new ScimResourceNotFoundException("User " + a.getUsername() + " has no ("
            + oidcId.issuer + ", " + oidcId.subject + ") oidc account to remove!");
      }
    }
  }
}
