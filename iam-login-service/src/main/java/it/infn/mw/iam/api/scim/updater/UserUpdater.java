package it.infn.mw.iam.api.scim.updater;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.converter.SshKeyConverter;
import it.infn.mw.iam.api.scim.converter.X509CertificateConverter;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation.ScimPatchOperationType;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;
import it.infn.mw.iam.persistence.repository.IamSamlIdRepository;
import it.infn.mw.iam.persistence.repository.IamSshKeyRepository;
import it.infn.mw.iam.persistence.repository.IamX509CertificateRepository;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKeyUtils;

@Component
public class UserUpdater implements Updater<IamAccount, ScimUser> {

  private final IamAccountRepository accountRepository;
  private final IamOidcIdRepository oidcIdRepository;
  private final IamX509CertificateRepository x509CertificateRepository;
  private final IamSshKeyRepository sshKeyRepository;
  private final IamSamlIdRepository samlIdRepository;

  private final OidcIdConverter oidcIdConverter;
  private final AddressConverter addressConverter;
  private final X509CertificateConverter certificateConverter;
  private final SshKeyConverter sshKeyConverter;
  private final SamlIdConverter samlIdConverter;

  @Autowired
  public UserUpdater(IamAccountRepository accountRepository, IamOidcIdRepository oidcIdRepository,
      IamX509CertificateRepository x509CertificateRepository, IamSshKeyRepository sshKeyRepository,
      IamSamlIdRepository samlIdRepository, OidcIdConverter oidcIdConverter,
      AddressConverter addressConverter, X509CertificateConverter certificateConverter,
      SshKeyConverter sshKeyConverter, SamlIdConverter samlIdConverter) {

    this.accountRepository = accountRepository;
    this.oidcIdRepository = oidcIdRepository;
    this.x509CertificateRepository = x509CertificateRepository;
    this.sshKeyRepository = sshKeyRepository;
    this.samlIdRepository = samlIdRepository;

    this.oidcIdConverter = oidcIdConverter;
    this.addressConverter = addressConverter;
    this.certificateConverter = certificateConverter;
    this.sshKeyConverter = sshKeyConverter;
    this.samlIdConverter = samlIdConverter;
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

    if (u.hasX509Certificates()) {

      u.getX509Certificates()
        .forEach(x509Cert -> patchX509Certificate(a, x509Cert, ScimPatchOperationType.add));
    }

    if (u.hasOidcIds()) {

      u.getIndigoUser().getOidcIds().forEach(oidcId -> addOidcId(a, oidcId));
    }

    if (u.hasSshKeys()) {

      u.getIndigoUser().getSshKeys().forEach(sshKey -> addSshKey(a, sshKey));
    }

    if (u.hasSamlIds()) {

      u.getIndigoUser().getSamlIds().forEach(samlId -> addSamlId(a, samlId));
    }

    a.touch();
    accountRepository.save(a);
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    if (u.hasX509Certificates()) {

      u.getX509Certificates()
        .forEach(x509Cert -> patchX509Certificate(a, x509Cert, ScimPatchOperationType.remove));
    }

    if (u.hasOidcIds()) {

      u.getIndigoUser().getOidcIds().forEach(oidcId -> removeOidcId(a, oidcId));
    }

    if (u.hasSshKeys()) {

      u.getIndigoUser().getSshKeys().forEach(sshKey -> removeSshKey(a, sshKey));
    }

    if (u.hasSamlIds()) {

      u.getIndigoUser().getSamlIds().forEach(samlId -> removeSamlId(a, samlId));
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

        throw new ScimPatchOperationNotSupported(
            "Unexpected ScimPatchOperationType " + action.toString());
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
        sshKeyToCreate.setFingerprint(RSAPublicKeyUtils.getSHA256Fingerprint(sshKey.getValue()));
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

  private void addSamlId(IamAccount owner, ScimSamlId samlIdToAdd) {

    Preconditions.checkNotNull(samlIdToAdd, "null saml id to add");
    Preconditions.checkNotNull(samlIdToAdd.getIdpId(), "saml id to add has null idpId");
    Preconditions.checkNotNull(samlIdToAdd.getUserId(), "saml id to add has null userId");

    Optional<IamAccount> samlAccount =
        accountRepository.findBySamlId(samlIdToAdd.getIdpId(), samlIdToAdd.getUserId());

    if (samlAccount.isPresent()) {

      if (samlAccount.get().getUuid().equals(owner.getUuid())) {

        return;

      } else {

        throw new ScimResourceExistsException(
            String.format("Saml account {},{} is already mapped to another user",
                samlIdToAdd.getIdpId(), samlIdToAdd.getUserId()));

      }

    } else {

      IamSamlId samlIdToCreate = samlIdConverter.fromScim(samlIdToAdd);
      samlIdToCreate.setAccount(owner);
      samlIdRepository.save(samlIdToCreate);

      owner.getSamlIds().add(samlIdToCreate);
    }

    return;
  }

  private void removeSamlId(IamAccount owner, ScimSamlId samlIdToRemove) {

    Preconditions.checkNotNull(samlIdToRemove, "Error: Saml account to remove is null");

    Optional<IamAccount> samlAccount =
        accountRepository.findBySamlId(samlIdToRemove.getIdpId(), samlIdToRemove.getUserId());

    if (samlAccount.isPresent()) {

      if (samlAccount.get().getUuid().equals(owner.getUuid())) {

        /* remove */
        IamSamlId toRemove = samlIdRepository
          .findByIdpIdAndUserId(samlIdToRemove.getIdpId(), samlIdToRemove.getUserId())
          .orElseThrow(() -> new ScimResourceNotFoundException(
              String.format("No Saml connect account found for {},{}", samlIdToRemove.getIdpId(),
                  samlIdToRemove.getUserId())));

        samlIdRepository.delete(toRemove);
        owner.getSamlIds().remove(toRemove);

      } else {

        throw new ScimResourceExistsException(
            String.format("Saml account {},{} is already mapped to another user",
                samlIdToRemove.getIdpId(), samlIdToRemove.getUserId()));

      }

    } else {

      throw new ScimResourceNotFoundException(
          String.format("User {} has no ({},{}) saml account to remove!", owner.getUsername(),
              samlIdToRemove.getIdpId(), samlIdToRemove.getUserId()));
    }

  }
}