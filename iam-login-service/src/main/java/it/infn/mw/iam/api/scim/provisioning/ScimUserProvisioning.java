package it.infn.mw.iam.api.scim.provisioning;

import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_OIDC_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_SAML_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_SSH_KEY;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_ADD_X509_CERTIFICATE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_OIDC_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_PICTURE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SAML_ID;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_SSH_KEY;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REMOVE_X509_CERTIFICATE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_ACTIVE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_EMAIL;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_FAMILY_NAME;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_GIVEN_NAME;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PASSWORD;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_PICTURE;
import static it.infn.mw.iam.api.scim.updater.UpdaterType.ACCOUNT_REPLACE_USERNAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.converter.SshKeyConverter;
import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.converter.X509CertificateConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.api.scim.updater.AccountUpdater;
import it.infn.mw.iam.api.scim.updater.UpdaterType;
import it.infn.mw.iam.api.scim.updater.factory.DefaultAccountUpdaterFactory;
import it.infn.mw.iam.audit.events.account.AccountReplacedEvent;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.core.user.exception.CredentialAlreadyBoundException;
import it.infn.mw.iam.core.user.exception.UserAlreadyExistsException;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class ScimUserProvisioning
    implements ScimProvisioning<ScimUser, ScimUser>, ApplicationEventPublisherAware {

  protected static final EnumSet<UpdaterType> SUPPORTED_UPDATER_TYPES = EnumSet.of(ACCOUNT_ADD_OIDC_ID,
      ACCOUNT_REMOVE_OIDC_ID, ACCOUNT_ADD_SAML_ID, ACCOUNT_REMOVE_SAML_ID, ACCOUNT_ADD_SSH_KEY,
      ACCOUNT_REMOVE_SSH_KEY, ACCOUNT_ADD_X509_CERTIFICATE, ACCOUNT_REMOVE_X509_CERTIFICATE,
      ACCOUNT_REPLACE_ACTIVE, ACCOUNT_REPLACE_EMAIL, ACCOUNT_REPLACE_FAMILY_NAME,
      ACCOUNT_REPLACE_GIVEN_NAME, ACCOUNT_REPLACE_PASSWORD, ACCOUNT_REPLACE_PICTURE,
      ACCOUNT_REPLACE_USERNAME, ACCOUNT_REMOVE_PICTURE);

  private final IamAccountService accountService;
  private final IamAccountRepository accountRepository;

  private final DefaultAccountUpdaterFactory updatersFactory;

  private final UserConverter userConverter;
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  public ScimUserProvisioning(IamAccountService accountService,
      IamAccountRepository accountRepository, PasswordEncoder passwordEncoder,
      UserConverter userConverter, OidcIdConverter oidcIdConverter, SamlIdConverter samlIdConverter,
      SshKeyConverter sshKeyConverter, X509CertificateConverter x509CertificateConverter) {

    this.accountService = accountService;
    this.accountRepository = accountRepository;
    this.userConverter = userConverter;
    this.updatersFactory = new DefaultAccountUpdaterFactory(passwordEncoder, accountRepository,
        oidcIdConverter, samlIdConverter, sshKeyConverter, x509CertificateConverter);
  }

  public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
    this.eventPublisher = publisher;
  }

  private void idSanityChecks(final String id) {

    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }

    if (id.trim().isEmpty()) {
      throw new IllegalArgumentException("id cannot be the empty string");
    }
  }

  @Override
  public ScimUser getById(final String id) {

    idSanityChecks(id);

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    return userConverter.toScim(account);

  }

  @Override
  public void delete(final String id) {

    idSanityChecks(id);

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    accountService.deleteAccount(account);
    
  }


  @Override
  public ScimUser create(final ScimUser user) {

    IamAccount newAccount = userConverter.fromScim(user);

    try {
      IamAccount account = accountService.createAccount(newAccount);
      return userConverter.toScim(account);
    } catch (CredentialAlreadyBoundException | UserAlreadyExistsException e) {
      throw new ScimResourceExistsException(e.getMessage(),e);
    }
  }

  @Override
  public ScimListResponse<ScimUser> list(final ScimPageRequest params) {

    if (params.getCount() == 0) {
      int userCount = accountRepository.countAllUsers();
      return new ScimListResponse<>(Collections.emptyList(), userCount, 0, 1);
    }

    OffsetPageable op = new OffsetPageable(params.getStartIndex(), params.getCount());

    Page<IamAccount> results = accountRepository.findAll(op);

    List<ScimUser> resources = new ArrayList<>();

    results.getContent().forEach(a -> resources.add(userConverter.toScim(a)));

    return new ScimListResponse<>(resources, results.getTotalElements(), resources.size(),
        op.getOffset() + 1);
  }

  @Override
  public ScimUser replace(final String uuid, final ScimUser scimItemToBeUpdated) {

    // user must exist
    IamAccount existingAccount = accountRepository.findByUuid(uuid)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + uuid + "'"));

    // username must be available
    final String username = scimItemToBeUpdated.getUserName();
    if (accountRepository.findByUsernameWithDifferentUUID(username, uuid).isPresent()) {
      throw new ScimResourceExistsException(
          "username " + username + " already assigned to another user");
    }

    // email must be unique
    final String updatedEmail = scimItemToBeUpdated.getEmails().get(0).getValue();
    if (accountRepository.findByEmailWithDifferentUUID(updatedEmail, uuid).isPresent()) {
      throw new ScimResourceExistsException(
          "email " + updatedEmail + " already assigned to another user");
    }

    IamAccount updatedAccount = userConverter.fromScim(scimItemToBeUpdated);

    updatedAccount.setId(existingAccount.getId());
    updatedAccount.setUuid(existingAccount.getUuid());
    updatedAccount.setCreationTime(existingAccount.getCreationTime());

    // If the active field was not provided in the input scim user,
    // use the value that was formerly set in the database
    if (scimItemToBeUpdated.getActive() == null) {
      updatedAccount.setActive(existingAccount.isActive());
    }

    updatedAccount.touch();

    accountRepository.save(updatedAccount);

    eventPublisher.publishEvent(new AccountReplacedEvent(this, updatedAccount, existingAccount,
        String.format("Replaced user %s with new user %s", updatedAccount.getUsername(),
            existingAccount.getUsername())));

    return userConverter.toScim(updatedAccount);
  }

  private void executePatchOperation(IamAccount account, ScimPatchOperation<ScimUser> op) {

    List<AccountUpdater> updaters = updatersFactory.getUpdatersForPatchOperation(account, op);
    List<AccountUpdater> updatesToPublish = new ArrayList<>();

    boolean oneUpdaterChangedAccount = false;

    for (AccountUpdater u : updaters) {
      if (!SUPPORTED_UPDATER_TYPES.contains(u.getType())) {
        throw new ScimPatchOperationNotSupported(u.getType().getDescription() + " not supported");
      }

      boolean lastUpdaterChangedAccount = u.update();

      oneUpdaterChangedAccount |= lastUpdaterChangedAccount;

      if (lastUpdaterChangedAccount) {
        updatesToPublish.add(u);
      }
    }

    if (oneUpdaterChangedAccount) {

      account.touch();
      accountRepository.save(account);
      for (AccountUpdater u : updatesToPublish) {
        u.publishUpdateEvent(this, eventPublisher);
      }

    }
  }

  @Override
  public void update(final String id, final List<ScimPatchOperation<ScimUser>> operations) {

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    operations.forEach(op -> executePatchOperation(account, op));
  }

}
