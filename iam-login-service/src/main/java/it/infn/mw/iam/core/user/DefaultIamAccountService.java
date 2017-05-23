package it.infn.mw.iam.core.user;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.audit.events.account.AccountCreatedEvent;
import it.infn.mw.iam.core.user.exception.CredentialAlreadyBoundException;
import it.infn.mw.iam.core.user.exception.InvalidCredentialException;
import it.infn.mw.iam.core.user.exception.UserAlreadyExistsException;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamAuthoritiesRepository;

@Service
public class DefaultIamAccountService implements IamAccountService {

  private final IamAccountRepository accountRepo;
  private final IamAuthoritiesRepository authoritiesRepo;
  private final PasswordEncoder passwordEncoder;
  private final ApplicationEventPublisher eventPublisher;

  @Autowired
  public DefaultIamAccountService(IamAccountRepository accountRepo,
      IamAuthoritiesRepository authoritiesRepo, PasswordEncoder passwordEncoder,
      ApplicationEventPublisher eventPublisher) {

    this.accountRepo = accountRepo;
    this.authoritiesRepo = authoritiesRepo;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public IamAccount createAccount(IamAccount account) {
    checkNotNull(account);

    final Date now = new Date();
    final String randomUuid = UUID.randomUUID().toString();
    
    newAccountSanityChecks(account);

    if (account.getCreationTime() == null){
      account.setCreationTime(now);
    }
    
    if (account.getUuid() == null) {
      account.setUuid(randomUuid);
    }
    
    account.setLastUpdateTime(now);

    account.getUserInfo().setEmailVerified(true);

    if (account.getPassword() == null) {
      account.setPassword(UUID.randomUUID().toString());
    }

    account.setPassword(passwordEncoder.encode(account.getPassword()));

    authoritiesRepo.findByAuthority("ROLE_USER")
      .map(a -> account.getAuthorities().add(a))
      .orElseThrow(
          () -> new IllegalStateException("ROLE_USER not found in database. This is a bug"));


    // Credentials sanity checks
    newAccountX509CertificatesSanityChecks(account);
    newAccountSshKeysSanityChecks(account);
    newAccountSamlIdsSanityChecks(account);
    newAccountOidcIdsSanityChecks(account);

    accountRepo.save(account);

    eventPublisher.publishEvent(new AccountCreatedEvent(this, account,
        "Account created for user " + account.getUsername()));

    return account;
  }

  @Override
  public IamAccount deleteAccount(IamAccount account) {
    return null;
  }

  private void newAccountOidcIdsSanityChecks(IamAccount account) {
    account.getOidcIds().forEach(this::oidcIdSanityChecks);
  }


  private void newAccountSamlIdsSanityChecks(IamAccount account) {
    account.getSamlIds().forEach(this::samlIdSanityChecks);
  }

  private void newAccountSanityChecks(IamAccount account) {
    checkNotNull(account.getUsername());
    checkNotNull(account.getUserInfo());
    checkNotNull(account.getUserInfo().getEmail());

    accountRepo.findByUsername(account.getUsername()).ifPresent(a -> {
      throw new UserAlreadyExistsException(
          String.format("A user with username '%s' already exists", a.getUsername()));
    });

    accountRepo.findByEmail(account.getUserInfo().getEmail()).ifPresent(a -> {
      throw new UserAlreadyExistsException(String
        .format("A user linked with email '%s' already exists", a.getUserInfo().getEmail()));
    });

  }

  private void newAccountSshKeysSanityChecks(IamAccount account) {

    if (account.hasSshKeys()) {

      account.getSshKeys().forEach(this::sshKeySanityChecks);

      final long count = account.getSshKeys().stream().filter(sshKey -> sshKey.isPrimary()).count();

      if (count > 1) {
        throw new InvalidCredentialException("Only one SSH key can be marked as primary");
      }

      if (count == 0) {
        account.getSshKeys().stream().findFirst().ifPresent(k -> k.setPrimary(true));
      }
    }
  }

  private void newAccountX509CertificatesSanityChecks(IamAccount account) {

    if (account.hasX509Certificates()) {

      account.getX509Certificates().forEach(this::x509CertificateSanityCheck);

      // FIXME: remove duplicate certificates, currently we don't have such a check
      
      final long count =
          account.getX509Certificates().stream().filter(cert -> cert.isPrimary()).count();

      if (count > 1) {
        throw new InvalidCredentialException("Only one X.509 certificate can be marked as primary");
      }

      if (count == 0) {
        account.getX509Certificates().stream().findFirst().ifPresent(c -> c.setPrimary(true));
      }
    }

  }

  private void oidcIdSanityChecks(IamOidcId oidcId) {
    checkNotNull(oidcId);
    checkNotNull(oidcId.getIssuer());
    checkNotNull(oidcId.getSubject());
    accountRepo.findByOidcId(oidcId.getIssuer(), oidcId.getSubject()).ifPresent(account -> {

      throw new CredentialAlreadyBoundException(String.format(
          "OIDC id '%s,%s' is already bound to a user", oidcId.getIssuer(), oidcId.getSubject()));
    });
  }

  private void samlIdSanityChecks(IamSamlId samlId) {

    checkNotNull(samlId);
    checkNotNull(samlId.getIdpId());
    checkNotNull(samlId.getUserId());
    checkNotNull(samlId.getAttributeId());
    accountRepo.findBySamlId(samlId).ifPresent(account -> {
      throw new CredentialAlreadyBoundException(
          String.format("SAML id '%s,%s,%s' already bound to a user", samlId.getIdpId(),
              samlId.getAttributeId(), samlId.getUserId()));
    });
  }

  private void sshKeySanityChecks(IamSshKey sshKey) {

    checkNotNull(sshKey);
    checkNotNull(sshKey.getValue());

    accountRepo.findBySshKeyValue(sshKey.getValue()).ifPresent(account -> {
      throw new CredentialAlreadyBoundException(
          String.format("SSH key '%s' already bound to a user", sshKey.getValue()));
    });
  }

  private void x509CertificateSanityCheck(IamX509Certificate cert) {
    checkNotNull(cert.getCertificate());
    checkNotNull(cert.getCertificateSubject());

    accountRepo.findByCertificate(cert.getCertificate()).ifPresent(c -> {
      throw new CredentialAlreadyBoundException(String
        .format("X509 certificate '%s' already bound to a user", cert.getCertificateSubject()));
    });

  }

}
