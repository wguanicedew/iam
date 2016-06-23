package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.X509CertificateConverter;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;
import it.infn.mw.iam.persistence.repository.IamX509CertificateRepository;

@Component
public class UserUpdater implements Updater<IamAccount, ScimUser> {

  private final IamAccountRepository accountRepository;
  private final IamOidcIdRepository oidcIdRepository;
  private final IamX509CertificateRepository x509CertificateRepository;

  private final OidcIdConverter oidcIdConverter;
  private final AddressConverter addressConverter;
  private final X509CertificateConverter certificateConverter;

  @Autowired
  public UserUpdater(IamAccountRepository accountRepository, IamOidcIdRepository oidcIdRepository,
      IamX509CertificateRepository x509CertificateRepository, OidcIdConverter oidcIdConverter,
      AddressConverter addressConverter, X509CertificateConverter certificateConverter) {

    this.accountRepository = accountRepository;
    this.oidcIdRepository = oidcIdRepository;
    this.x509CertificateRepository = x509CertificateRepository;

    this.oidcIdConverter = oidcIdConverter;
    this.addressConverter = addressConverter;
    this.certificateConverter = certificateConverter;
  }

  public void update(IamAccount account, List<ScimPatchOperation<ScimUser>> operations) {

    operations.forEach(op -> {
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
      accountRepository.save(account);
    });
  }

  private void addNotNullInfo(IamAccount a, ScimUser u) {

    patchUserName(a, u.getUserName());
    patchActive(a, u.getActive());
    patchName(a, u.getName());
    patchEmail(a, u.getEmails());
    patchAddress(a, u.getAddresses());
    patchPassword(a, u.getPassword());
    patchX509Certificates(a, u.getX509Certificates(), ScimPatchOperationType.add);

    if (u.getIndigoUser() != null) {

      addOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }

    a.touch();
    accountRepository.save(a);
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    patchX509Certificates(a, u.getX509Certificates(), ScimPatchOperationType.remove);

    if (u.getIndigoUser() != null) {

      removeOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }

    a.touch();
    accountRepository.save(a);
  }

  private void replaceNotNullInfo(IamAccount a, ScimUser u) {

    patchUserName(a, u.getUserName());
    patchActive(a, u.getActive());
    patchName(a, u.getName());
    patchEmail(a, u.getEmails());
    patchAddress(a, u.getAddresses());
    patchPassword(a, u.getPassword());

    a.touch();
    accountRepository.save(a);

  }

  private void patchPassword(IamAccount a, String password) {

    a.setPassword(password != null ? password : a.getPassword());

  }

  private void patchX509Certificates(IamAccount a, List<ScimX509Certificate> x509Certificates, ScimPatchOperationType action) {

    if (x509Certificates != null) {

      for (ScimX509Certificate cert : x509Certificates) {

        patchX509Certificate(a, cert, action);
      }
    }

  }

  private void patchX509Certificate(IamAccount a, ScimX509Certificate cert,
      ScimPatchOperationType action) {
    
    Preconditions.checkNotNull("X509Certificate is null", cert);

    switch (action) {

      case add:

        IamX509Certificate toAdd = certificateConverter.fromScim(cert);

        if (toAdd.getAccount() == null) {

          toAdd.setAccount(a);
          x509CertificateRepository.save(toAdd);

          a.getX509Certificates().add(toAdd);

        } else if (toAdd.getAccount().getUuid() != a.getUuid()) {

          throw new ScimResourceExistsException("Cannot add x509 certificate to user "
              + a.getUsername() + " because it's already associated to another user");
        }
        break;
      case remove:

        IamX509Certificate toRemove = x509CertificateRepository.findByCertificate(cert.getValue())
          .orElseThrow(() -> new ScimResourceNotFoundException("No x509 certificate found"));

        if (toRemove.getAccount().getUuid().equals(a.getUuid())) {

          x509CertificateRepository.delete(toRemove);
          a.getX509Certificates().remove(toRemove);

        } else {

          throw new ScimResourceExistsException("Cannot remove x509 certificate to user "
              + a.getUsername() + " because it's owned by another user");
        }

        break;
      default:
        break;
    }

  }

  private void patchAddress(IamAccount a, List<ScimAddress> addresses) {

    if (addresses != null && !addresses.isEmpty()) {

      a.getUserInfo().setAddress(addressConverter.fromScim(addresses.get(0)));
    }
  }

  private void patchActive(IamAccount a, Boolean active) {

    if (active != null) {
      if (a.isActive() ^ active) {

        a.setActive(active);
      }
    }
  }

  private void patchUserName(IamAccount a, String userName) {

    if (userName != null) {

      a.setUsername(userName);
    }
  }

  private void patchName(IamAccount a, ScimName name) {

    if (name != null) {

      a.getUserInfo().setFamilyName(
          name.getFamilyName() != null ? name.getFamilyName() : a.getUserInfo().getFamilyName());
      a.getUserInfo().setGivenName(
          name.getGivenName() != null ? name.getGivenName() : a.getUserInfo().getGivenName());
      a.getUserInfo().setMiddleName(
          name.getMiddleName() != null ? name.getMiddleName() : a.getUserInfo().getGivenName());
      a.getUserInfo()
        .setName(name.getFormatted() != null ? name.getFormatted() : a.getUserInfo().getName());

    }
  }

  private void patchEmail(IamAccount a, List<ScimEmail> emails) {

    if (emails != null && !emails.isEmpty()) {

      a.getUserInfo().setEmail(emails.get(0).getValue());
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

        oidcIdRepository.delete(current);
        a.getOidcIds().remove(current);

      } else {
        throw new ScimResourceNotFoundException("User " + a.getUsername() + " has no ("
            + oidcId.issuer + ", " + oidcId.subject + ") oidc account to remove!");
      }
    }
  }
}
