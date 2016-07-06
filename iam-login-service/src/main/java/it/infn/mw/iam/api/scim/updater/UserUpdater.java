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
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimAddress;
import it.infn.mw.iam.api.scim.model.ScimEmail;
import it.infn.mw.iam.api.scim.model.ScimName;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
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

      u.getX509Certificates().forEach(x509Cert -> addX509Certificate(a, x509Cert));

      /* if there's no primary x509 certificate, set the first as it */
      if (a.getX509Certificates().stream().noneMatch(cert -> cert.isPrimary())) {

        a.getX509Certificates().stream().findFirst().get().setPrimary(true);
      }
    }

    if (u.hasOidcIds()) {

      u.getIndigoUser().getOidcIds().forEach(oidcId -> addOidcId(a, oidcId));
    }

    if (u.hasSshKeys()) {

      u.getIndigoUser().getSshKeys().forEach(sshKey -> addSshKey(a, sshKey));

      /* if there's no primary ssh key, set the first as it */
      if (a.getSshKeys().stream().noneMatch(sshKey -> sshKey.isPrimary())) {

        a.getSshKeys().stream().findFirst().get().setPrimary(true);
      }
    }

    if (u.hasSamlIds()) {

      u.getIndigoUser().getSamlIds().forEach(samlId -> addSamlId(a, samlId));
    }

    a.touch();
    accountRepository.save(a);
  }

  private void removeNotNullInfo(IamAccount a, ScimUser u) {

    if (u.hasX509Certificates()) {

      u.getX509Certificates().forEach(x509Cert -> removeX509Certificate(a, x509Cert));
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

    if (u.hasX509Certificates()) {

      u.getX509Certificates().forEach(x509Cert -> replaceX509Certificate(a, x509Cert));
    }

    if (u.hasSshKeys()) {

      u.getIndigoUser().getSshKeys().forEach(sshKey -> replaceSshKey(a, sshKey));

      /* if there's no primary ssh key, set the first as it */
      if (a.getSshKeys().stream().noneMatch(sshKey -> sshKey.isPrimary())) {

        a.getSshKeys().stream().findFirst().get().setPrimary(true);
      }
    }

    a.touch();
    accountRepository.save(a);

  }

  private void patchPassword(IamAccount a, String password) {

    a.setPassword(password != null ? password : a.getPassword());

  }

  private void addX509Certificate(IamAccount a, ScimX509Certificate cert) {

    Preconditions.checkNotNull(cert, "X509Certificate is null");

    IamX509Certificate toAdd = certificateConverter.fromScim(cert);

    Optional<IamAccount> x509Account =
        accountRepository.findByCertificateSubject(toAdd.getCertificateSubject());

    if (!x509Account.isPresent()) {

      toAdd.setAccount(a);
      x509CertificateRepository.save(toAdd);

      a.getX509Certificates().add(toAdd);


    } else {

      if (x509Account.get().equals(a)) {

        throw new ScimResourceExistsException(String.format("Duplicated x509 certificate (%s,%s)",
            toAdd.getLabel(), toAdd.getCertificateSubject()));

      } else {

        throw new ScimResourceExistsException(
            String.format("Cannot add x509 certificate: already mapped to another user"));
      }
    }
  }

  private void replaceX509Certificate(IamAccount a, ScimX509Certificate cert) {

    Preconditions.checkNotNull(cert, "X509Certificate is null");

    Optional<IamX509Certificate> x509Cert =
        x509CertificateRepository.findByCertificate(cert.getValue());

    if (x509Cert.isPresent()) {

      if (a.equals(x509Cert.get().getAccount())) {

        /* update display and primary */
        if (cert.getDisplay() != null) {
          x509Cert.get().setLabel(cert.getDisplay());
        }
        if (cert.isPrimary() != null) {
          x509Cert.get().setPrimary(cert.isPrimary());
        }

        x509CertificateRepository.save(x509Cert.get());

      } else {

        throw new ScimResourceExistsException(
            String.format("Cannot add x509 certificate: already mapped to another user"));
      }

    } else {

      throw new ScimResourceNotFoundException(
          String.format("Cannot find x509 certificate with vakue %s", cert.getValue()));
    }
  }

  private void removeX509Certificate(IamAccount a, ScimX509Certificate cert) {

    Preconditions.checkNotNull(cert, "X509Certificate is null");

    IamX509Certificate toRemove = x509CertificateRepository.findByCertificate(cert.getValue())
      .orElseThrow(() -> new ScimResourceNotFoundException("No x509 certificate found"));

    if (toRemove.getAccount().equals(a)) {

      x509CertificateRepository.delete(toRemove);
      a.getX509Certificates().remove(toRemove);

    } else {

      throw new ScimResourceExistsException(
          "Cannot remove x509 certificate: already mapped to another user");
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

    Preconditions.checkNotNull(oidcIdToAdd, "Add OpenID account: null OpenID account");
    Preconditions.checkNotNull(oidcIdToAdd.getIssuer(), "Add OpenID account: null issuer");
    Preconditions.checkNotNull(oidcIdToAdd.getSubject(), "Add OpenID account: null subject");

    Optional<IamAccount> oidcAccount =
        accountRepository.findByOidcId(oidcIdToAdd.getIssuer(), oidcIdToAdd.getSubject());

    if (!oidcAccount.isPresent()) {

      IamOidcId oidcIdToCreate = oidcIdConverter.fromScim(oidcIdToAdd);
      oidcIdToCreate.setAccount(owner);

      oidcIdRepository.save(oidcIdToCreate);
      owner.getOidcIds().add(oidcIdToCreate);

    } else {

      if (oidcAccount.get().equals(owner)) {

        throw new ScimResourceExistsException(String.format("Duplicated Open ID account (%s,%s)",
            oidcIdToAdd.getIssuer(), oidcIdToAdd.getSubject()));

      } else {

        throw new ScimResourceExistsException(
            String.format("OpenID account (%s,%s) is already mapped to another user",
                oidcIdToAdd.getIssuer(), oidcIdToAdd.getSubject()));

      }
    }
  }

  private void removeOidcId(IamAccount owner, ScimOidcId oidcIdToRemove) {

    Preconditions.checkNotNull(oidcIdToRemove, "Remove OpenID account: null OpenID account");

    IamAccount oidcAccount =
        accountRepository.findByOidcId(oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject())
          .orElseThrow(() -> new ScimResourceNotFoundException(
              String.format("User {} has no (%s,%s) oidc account to remove!", owner.getUsername(),
                  oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject())));

    if (oidcAccount.equals(owner)) {

      /* remove */
      IamOidcId toRemove = oidcIdRepository
        .findByIssuerAndSubject(oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject())
        .orElseThrow(() -> new ScimResourceNotFoundException(
            String.format("No Open ID connect account found for (%s,%s)",
                oidcIdToRemove.getSubject(), oidcIdToRemove.getIssuer())));

      oidcIdRepository.delete(toRemove);
      owner.getOidcIds().remove(toRemove);

    } else {

      throw new ScimResourceExistsException(
          String.format("OpenID account (%s,%s) is already mapped to another user",
              oidcIdToRemove.getIssuer(), oidcIdToRemove.getSubject()));

    }

  }

  private void addSshKey(IamAccount owner, ScimSshKey sshKey) throws ScimException {

    Preconditions.checkNotNull(sshKey, "Add ssh key: null ssh key");
    Preconditions.checkNotNull(sshKey.getValue(), "Add ssh key: null key value");

    Optional<IamAccount> sshKeyAccount = accountRepository.findBySshKeyValue(sshKey.getValue());

    if (!sshKeyAccount.isPresent()) {

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

    } else {

      if (sshKeyAccount.get().equals(owner)) {

        throw new ScimResourceExistsException(String.format("Duplicated SSH Key (%s,%s)",
            sshKey.getDisplay(), sshKey.getFingerprint()));

      } else {

        throw new ScimResourceExistsException(
            String.format("Ssh key (%s,%s) is already mapped to another user", sshKey.getDisplay(),
                sshKey.getFingerprint()));

      }
    }
  }

  private void replaceSshKey(IamAccount owner, ScimSshKey sshKeyToReplace) throws ScimException {

    Preconditions.checkNotNull(sshKeyToReplace, "Replace ssh key: null ssh key");

    Optional<IamSshKey> iamSshKey;

    if (sshKeyToReplace.getFingerprint() != null) {

      iamSshKey = sshKeyRepository.findByFingerprint(sshKeyToReplace.getFingerprint());

    } else if (sshKeyToReplace.getValue() != null) {

      iamSshKey = sshKeyRepository.findByValue(sshKeyToReplace.getValue());

    } else if (sshKeyToReplace.getDisplay() != null) {

      iamSshKey = sshKeyRepository.findByLabel(sshKeyToReplace.getDisplay());

    } else {

      throw new ScimException(
          String.format("Unable to load ssh key from persistence with %s", sshKeyToReplace));
    }

    if (iamSshKey.isPresent()) {

      if (sshKeyToReplace.getDisplay() != null) {
        iamSshKey.get().setLabel(sshKeyToReplace.getDisplay());
      }

      if (sshKeyToReplace.isPrimary() != null) {
        iamSshKey.get().setPrimary(sshKeyToReplace.isPrimary());
      }

      sshKeyRepository.save(iamSshKey.get());

    } else {

      throw new ScimResourceNotFoundException(
          String.format("Ssh key (%s) to replace not found", sshKeyToReplace));

    }
  }

  private void removeSshKey(IamAccount owner, ScimSshKey sshKeyToRemove) {

    Preconditions.checkNotNull(sshKeyToRemove, "Remove ssh key: null ssh key");

    Optional<IamSshKey> iamSshKey;

    if (sshKeyToRemove.getFingerprint() != null) {

      iamSshKey = sshKeyRepository.findByFingerprint(sshKeyToRemove.getFingerprint());

    } else if (sshKeyToRemove.getValue() != null) {

      iamSshKey = sshKeyRepository.findByValue(sshKeyToRemove.getValue());

    } else if (sshKeyToRemove.getDisplay() != null) {

      iamSshKey = sshKeyRepository.findByLabel(sshKeyToRemove.getDisplay());

    } else {

      throw new ScimException(
          String.format("Unable to load ssh key from persistence with %s", sshKeyToRemove));
    }

    if (iamSshKey.isPresent()) {

      if (iamSshKey.get().getAccount().equals(owner)) {

        /* remove */
        sshKeyRepository.delete(iamSshKey.get());
        owner.getSshKeys().remove(iamSshKey.get());

      } else {

        throw new ScimResourceExistsException(String
          .format("Ssh key %s is already mapped to another user", sshKeyToRemove.getFingerprint()));

      }

    } else {

      throw new ScimResourceNotFoundException(String.format("User %s has no %s ssh key to remove!",
          owner.getUsername(), sshKeyToRemove.getFingerprint()));
    }
  }

  private void addSamlId(IamAccount owner, ScimSamlId samlIdToAdd) {

    Preconditions.checkNotNull(samlIdToAdd, "Add Saml Id: null Saml Id");
    Preconditions.checkNotNull(samlIdToAdd.getIdpId(), "Add Saml Id: null idpId");
    Preconditions.checkNotNull(samlIdToAdd.getUserId(), "Add Saml Id: null userId");

    Optional<IamAccount> samlAccount =
        accountRepository.findBySamlId(samlIdToAdd.getIdpId(), samlIdToAdd.getUserId());

    if (!samlAccount.isPresent()) {

      IamSamlId samlIdToCreate = samlIdConverter.fromScim(samlIdToAdd);
      samlIdToCreate.setAccount(owner);
      samlIdRepository.save(samlIdToCreate);

      owner.getSamlIds().add(samlIdToCreate);

    } else {

      if (samlAccount.get().equals(owner)) {

        throw new ScimResourceExistsException(String.format("Duplicated Saml Id (%s,%s)",
            samlIdToAdd.getIdpId(), samlIdToAdd.getUserId()));

      } else {

        throw new ScimResourceExistsException(
            String.format("Saml account (%s,%s) is already mapped to another user",
                samlIdToAdd.getIdpId(), samlIdToAdd.getUserId()));

      }
    }
  }

  private void removeSamlId(IamAccount owner, ScimSamlId samlIdToRemove) {

    Preconditions.checkNotNull(samlIdToRemove, "Remove Saml Id: null saml id");

    Optional<IamAccount> samlAccount =
        accountRepository.findBySamlId(samlIdToRemove.getIdpId(), samlIdToRemove.getUserId());

    if (samlAccount.isPresent()) {

      if (samlAccount.get().equals(owner)) {

        /* remove */
        IamSamlId toRemove = samlIdRepository
          .findByIdpIdAndUserId(samlIdToRemove.getIdpId(), samlIdToRemove.getUserId())
          .orElseThrow(() -> new ScimResourceNotFoundException(
              String.format("No Saml connect account found for (%s,%s)", samlIdToRemove.getIdpId(),
                  samlIdToRemove.getUserId())));

        samlIdRepository.delete(toRemove);
        owner.getSamlIds().remove(toRemove);

      } else {

        throw new ScimResourceExistsException(
            String.format("Saml account (%s,%s) is already mapped to another user",
                samlIdToRemove.getIdpId(), samlIdToRemove.getUserId()));

      }

    } else {

      throw new ScimResourceNotFoundException(
          String.format("User %s has no (%s,%s) saml account to remove!", owner.getUsername(),
              samlIdToRemove.getIdpId(), samlIdToRemove.getUserId()));
    }

  }
}
