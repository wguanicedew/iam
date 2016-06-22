package it.infn.mw.iam.api.scim.updater;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    addOrReplaceUserNameIfNotNull(a, u.getUserName());
    setActiveIfNotNull(a, u.getActive());
    addOrReplaceNameIfNotNull(a, u.getName());
    addOrReplaceEmailIfNotNull(a, u.getEmails());
    addOrReplaceAddressIfNotNull(a, u.getAddresses());

    if (u.getX509Certificates() != null) {
      addX509Certificates(a, u.getX509Certificates());
    }

    if (u.getIndigoUser() != null) {

      addOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }
  }

  private void addX509Certificates(IamAccount a, List<ScimX509Certificate> x509Certificates) {

    if (x509Certificates != null) {

      for (ScimX509Certificate cert : x509Certificates) {

        addX509CertificateIfNotNull(a, cert);
      }
    }
  }

  private void removeX509Certificates(IamAccount a, List<ScimX509Certificate> x509Certificates) {

    if (x509Certificates != null) {

      for (ScimX509Certificate cert : x509Certificates) {

        removeX509CertificateIfNotNull(a, cert);
      }
    }

  }

  private void addX509CertificateIfNotNull(IamAccount a, ScimX509Certificate cert) {

    if (cert != null) {

      IamX509Certificate current = certificateConverter.fromScim(cert);

      if (current.getAccount() == null) {

        current.setAccount(a);
        x509CertificateRepository.save(current);

        a.getX509Certificates().add(current);
        a.touch();
        accountRepository.save(a);

      } else if (current.getAccount().getUuid() != a.getUuid()) {

        throw new ScimResourceExistsException("Cannot add x509 certificate to user "
            + a.getUsername() + " because it's already associated to another user");
      }
    }

  }

  private void removeX509CertificateIfNotNull(IamAccount a, ScimX509Certificate cert) {

    if (cert != null) {

      IamX509Certificate current = x509CertificateRepository.findByCertificate(cert.getValue())
          .orElseThrow(() -> new ScimResourceNotFoundException("No x509 certificate found"));

      if (current.getAccount().getUuid().equals(a.getUuid())) {

        a.getX509Certificates().remove(current);
        a.touch();
        accountRepository.save(a);
        x509CertificateRepository.delete(current);

      } else {

        throw new ScimResourceExistsException("Cannot remove x509 certificate to user "
            + a.getUsername() + " because it's owned by another user");
      }
    }

  }

  private void addOrReplaceAddressIfNotNull(IamAccount a, List<ScimAddress> addresses) {

    if (addresses != null && !addresses.isEmpty()) {

      a.getUserInfo().setAddress(addressConverter.fromScim(addresses.get(0)));
      a.touch();
      accountRepository.save(a);
    }
  }

  private void setActiveIfNotNull(IamAccount a, Boolean active) {

    if (active != null) {
      if (a.isActive() ^ active) {

        a.setActive(active);
        a.touch();
        accountRepository.save(a);
      }
    }
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    removeX509Certificates(a, u.getX509Certificates());

    if (u.getIndigoUser() != null) {

      removeOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
    }
  }

  private void replaceNotNullInfo(IamAccount a, ScimUser u) {

    addOrReplaceUserNameIfNotNull(a, u.getUserName());
    setActiveIfNotNull(a, u.getActive());
    addOrReplaceNameIfNotNull(a, u.getName());
    addOrReplaceEmailIfNotNull(a, u.getEmails());
    addOrReplaceAddressIfNotNull(a, u.getAddresses());

  }

  private void addOrReplaceUserNameIfNotNull(IamAccount a, String userName) {

    if (userName != null) {

      a.setUsername(userName);
      a.touch();
      accountRepository.save(a);
    }
  }

  private void addOrReplaceNameIfNotNull(IamAccount a, ScimName name) {

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
      accountRepository.save(a);
    }
  }

  private void addOrReplaceEmailIfNotNull(IamAccount a, List<ScimEmail> emails) {

    if (emails != null && !emails.isEmpty()) {

      a.getUserInfo().setEmail(emails.get(0).getValue());
      a.touch();
      accountRepository.save(a);
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
