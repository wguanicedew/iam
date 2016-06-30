package it.infn.mw.iam.api.scim.updater;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SshKeyConverter;
import it.infn.mw.iam.api.scim.converter.X509CertificateConverter;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;
import it.infn.mw.iam.persistence.repository.IamSshKeyRepository;
import it.infn.mw.iam.persistence.repository.IamX509CertificateRepository;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKey;

@Component
public class UserUpdater implements Updater<IamAccount, ScimUser> {

  private final IamAccountRepository accountRepository;
  private final IamOidcIdRepository oidcIdRepository;
  private final IamX509CertificateRepository x509CertificateRepository;
  private final IamSshKeyRepository sshKeyRepository;

  private final OidcIdConverter oidcIdConverter;
  private final AddressConverter addressConverter;
  private final X509CertificateConverter certificateConverter;
  private final SshKeyConverter sshKeyConverter;

  @Autowired
  public UserUpdater(IamAccountRepository accountRepository, IamOidcIdRepository oidcIdRepository,
      IamX509CertificateRepository x509CertificateRepository, IamSshKeyRepository sshKeyRepository,
      OidcIdConverter oidcIdConverter, AddressConverter addressConverter,
      X509CertificateConverter certificateConverter, SshKeyConverter sshKeyConverter) {

    this.accountRepository = accountRepository;
    this.oidcIdRepository = oidcIdRepository;
    this.x509CertificateRepository = x509CertificateRepository;
    this.sshKeyRepository = sshKeyRepository;

    this.oidcIdConverter = oidcIdConverter;
    this.addressConverter = addressConverter;
    this.certificateConverter = certificateConverter;
    this.sshKeyConverter = sshKeyConverter;
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
      addSshKeysIfNotNull(a, u.getIndigoUser().getSshKeys());
    }

    a.touch();
    accountRepository.save(a);
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    patchX509Certificates(a, u.getX509Certificates(), ScimPatchOperationType.remove);

    if (u.getIndigoUser() != null) {

      removeOidcIdsIfNotNull(a, u.getIndigoUser().getOidcIds());
      removeSshKeysIfNotNull(a, u.getIndigoUser().getSshKeys());
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

  private void patchX509Certificates(IamAccount a, List<ScimX509Certificate> x509Certificates,
      ScimPatchOperationType action) {

    if (x509Certificates != null) {

      for (ScimX509Certificate cert : x509Certificates) {

        patchX509Certificate(a, cert, action);
      }
    }

  }

  private void patchX509Certificate(IamAccount a, ScimX509Certificate cert,
      ScimPatchOperationType action) {

    Assert.assertNotNull("X509Certificate is null", cert);

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

        addOidcId(a, oidc);
      }
    }
  }

  private void addOidcId(IamAccount owner, ScimOidcId oidcIdToAdd) {

    Preconditions.checkNotNull(oidcIdToAdd, "Error: OpenID account to add is null");

    Optional<IamAccount> oidcAccount =
        accountRepository.findByOidcId(oidcIdToAdd.getIssuer(), oidcIdToAdd.getSubject());

    if (oidcAccount.isPresent()) {

      if (oidcAccount.get().getUuid().equals(owner.getUuid())) {

        return;

      } else {

        throw new ScimResourceExistsException(
            String.format("OpenID account {},{} is already mapped to another user",
                oidcIdToAdd.getIssuer(), oidcIdToAdd.getSubject()));

      }

    } else {

      IamOidcId oidcIdToCreate = oidcIdConverter.fromScim(oidcIdToAdd);
      oidcIdToCreate.setAccount(owner);
      oidcIdRepository.save(oidcIdToCreate);

      owner.getOidcIds().add(oidcIdToCreate);
    }
  }

  private void removeOidcIdsIfNotNull(IamAccount a, List<ScimOidcId> oidcIds) {

    if (oidcIds != null) {

      for (ScimOidcId oidc : oidcIds) {

        removeOidcId(a, oidc);
      }
    }
  }

  private void removeOidcId(IamAccount owner, ScimOidcId oidcIdToRemove) {

    Preconditions.checkNotNull(oidcIdToRemove, "Error: OpenID account to remove is null");

    Optional<IamAccount> oidcAccount =
        accountRepository.findByOidcId(oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject());

    if (oidcAccount.isPresent()) {

      if (oidcAccount.get().getUuid().equals(owner.getUuid())) {

        /* remove */
        IamOidcId toRemove = oidcIdRepository
          .findByIssuerAndSubject(oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject())
          .orElseThrow(() -> new ScimResourceNotFoundException(
              String.format("No Open ID connect account found for {},{}",
                  oidcIdToRemove.getSubject(), oidcIdToRemove.getIssuer())));

        oidcIdRepository.delete(toRemove);
        owner.getOidcIds().remove(toRemove);

      } else {

        throw new ScimResourceExistsException(
            String.format("OpenID account {},{} is already mapped to another user",
                oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject()));

      }

    } else {

      throw new ScimResourceNotFoundException(
          String.format("User {} has no ({},{}) oidc account to remove!", owner.getUsername(),
              oidcIdToRemove.issuer, oidcIdToRemove.subject));
    }
  }

  private void addSshKeysIfNotNull(IamAccount a, List<ScimSshKey> sshKeys) {

    if (sshKeys != null) {

      for (ScimSshKey sshKey : sshKeys) {

        addSshKey(a, sshKey);
      }
    }
  }

  private void addSshKey(IamAccount owner, ScimSshKey sshKey) throws ScimException {

    Preconditions.checkNotNull(sshKey, "Error: SSH key to add is null");
    Preconditions.checkNotNull(sshKey.getValue(), "Unable to add ssh key with key value null");

    Optional<IamAccount> sshKeyAccount = accountRepository.findBySshKeyValue(sshKey.getValue());

    if (sshKeyAccount.isPresent()) {

      if (sshKeyAccount.get().getUuid().equals(owner.getUuid())) {

        return;

      } else {

        throw new ScimResourceExistsException(
            String.format("Ssh key {} is already mapped to another user", sshKey.getFingerprint()));

      }

    } else {

      IamSshKey sshKeyToCreate = sshKeyConverter.fromScim(sshKey);
      sshKeyToCreate.setAccount(owner);

      try {
        RSAPublicKey key = new RSAPublicKey(sshKey.getValue());
        sshKeyToCreate.setFingerprint(key.getSHA256Fingerprint());
      } catch (InvalidSshKeyException e) {
        throw new ScimException(e.getMessage());
      }

      if (sshKeyToCreate.getLabel() == null) {
        sshKeyToCreate.setLabel(owner.getUsername() + "'s personal ssh key");
      }

      sshKeyRepository.save(sshKeyToCreate);

      owner.getSshKeys().add(sshKeyToCreate);
    }
  }

  private void removeSshKeysIfNotNull(IamAccount a, List<ScimSshKey> sshKeys) {

    if (sshKeys != null) {

      for (ScimSshKey sshKey : sshKeys) {

        removeSshKey(a, sshKey);
      }
    }
  }

  private void removeSshKey(IamAccount owner, ScimSshKey sshKeyToRemove) {

    Preconditions.checkNotNull(sshKeyToRemove, "Error: ssh key to remove is null");

    Optional<IamSshKey> iamSshKey;

    if (sshKeyToRemove.getFingerprint() != null) {

      iamSshKey = sshKeyRepository.findByFingerprint(sshKeyToRemove.getFingerprint());

    } else if (sshKeyToRemove.getDisplay() != null) {

      iamSshKey = sshKeyRepository.findByLabel(sshKeyToRemove.getDisplay());

    } else if (sshKeyToRemove.getValue() != null) {

      iamSshKey = sshKeyRepository.findByValue(sshKeyToRemove.getValue());

    } else {

      throw new ScimException(
          "Unable to load ssh key from persistence with " + sshKeyToRemove.toString());
    }

    if (iamSshKey.isPresent()) {

      if (iamSshKey.get().getAccount().getUuid().equals(owner.getUuid())) {

        /* remove */
        sshKeyRepository.delete(iamSshKey.get());
        owner.getSshKeys().remove(iamSshKey.get());

      } else {

        throw new ScimResourceExistsException(String
          .format("Ssh key {} is already mapped to another user", sshKeyToRemove.getFingerprint()));

      }

    } else {

      throw new ScimResourceNotFoundException(String.format("User {} has no {} ssh key to remove!",
          owner.getUsername(), sshKeyToRemove.getFingerprint()));
    }
  }
}
