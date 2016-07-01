package it.infn.mw.iam.api.scim.provisioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.AddressConverter;
import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimPatchOperation;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;
import it.infn.mw.iam.api.scim.updater.UserUpdater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamAuthoritiesRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;
import it.infn.mw.iam.persistence.repository.IamSshKeyRepository;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKeyUtils;

@Service
public class ScimUserProvisioning implements ScimProvisioning<ScimUser, ScimUser> {

  private final UserConverter converter;
  private final AddressConverter addressConverter;

  private final UserUpdater updater;

  private final IamAccountRepository accountRepository;
  private final IamOidcIdRepository oidcIdRepository;
  private final IamSshKeyRepository sshKeyRepository;

  private final IamAuthoritiesRepository authorityRepository;

  @Autowired
  public ScimUserProvisioning(UserConverter converter, AddressConverter addressConverter,
      UserUpdater updater, IamAccountRepository accountRepo, IamOidcIdRepository oidcIdRepository,
      IamSshKeyRepository sshKeyRepository, IamAuthoritiesRepository authorityRepo) {

    this.converter = converter;
    this.addressConverter = addressConverter;
    this.updater = updater;
    this.accountRepository = accountRepo;
    this.oidcIdRepository = oidcIdRepository;
    this.sshKeyRepository = sshKeyRepository;
    this.authorityRepository = authorityRepo;

  }

  private void idSanityChecks(String id) {

    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }

    if (id.trim().isEmpty()) {
      throw new IllegalArgumentException("id cannot be the empty string");
    }
  }

  @Override
  public ScimUser getById(String id) {

    idSanityChecks(id);

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    return converter.toScim(account);

  }

  @Override
  public void delete(String id) {

    idSanityChecks(id);

    IamAccount account = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    accountRepository.delete(account);

  }

  @Override
  public ScimUser create(ScimUser user) {

    Date creationTime = new Date();

    accountRepository.findByUsername(user.getUserName()).ifPresent(a -> {
      throw new ScimResourceExistsException("userName is already taken: " + a.getUsername());
    });

    String uuid = UUID.randomUUID().toString();

    IamAccount account = converter.fromScim(user);
    account.setUuid(uuid);
    account.setCreationTime(creationTime);
    account.setLastUpdateTime(creationTime);
    account.setUsername(user.getUserName());
    account.setActive(user.getActive() == null ? false : user.getActive());

    if (account.getPassword() == null) {
      account.setPassword(UUID.randomUUID().toString());
    }

    authorityRepository.findByAuthority("ROLE_USER")
      .map(a -> account.getAuthorities().add(a))
      .orElseThrow(
          () -> new IllegalStateException("ROLE_USER not found in database. This is a bug"));

    IamUserInfo userInfo = new IamUserInfo();

    if (user.getName() != null) {
      userInfo.setGivenName(user.getName().getGivenName());
      userInfo.setFamilyName(user.getName().getFamilyName());
      userInfo.setMiddleName(user.getName().getMiddleName());
    }

    if (user.getAddresses() != null && !user.getAddresses().isEmpty()) {
      userInfo.setAddress(addressConverter.fromScim(user.getAddresses().get(0)));
    }

    if (!user.getEmails().isEmpty()) {
      userInfo.setEmail(user.getEmails().get(0).getValue());
    }
    account.setUserInfo(userInfo);

    if (account.hasOidcIds()) {

      account.getOidcIds().forEach((oidcId) -> {
        checkOidcIdNotExists(oidcId);
      });
    }

    if (account.hasSshKeys()) {

      account.getSshKeys().forEach((sshKey) -> {
        checkSshKeyNotExists(sshKey);
      });

      if (!account.getSshKeys()
        .stream()
        .filter(sshKey -> sshKey.isPrimary())
        .findFirst()
        .isPresent()) {

        account.getSshKeys().forEach(sshKey -> sshKey.setPrimary(false));
        account.getSshKeys().get(0).setPrimary(true);
      }
    }

    if (account.hasSamlIds()) {
      // TO-DO
    }

    accountRepository.save(account);

    return converter.toScim(account);
  }

  private void checkOidcIdNotExists(IamOidcId oidcId) {

    if (oidcIdRepository.findByIssuerAndSubject(oidcId.getIssuer(), oidcId.getSubject())
      .isPresent()) {

      throw new ScimResourceExistsException("OIDC id " + oidcId.getIssuer() + ","
          + oidcId.getSubject() + " is already mapped to another user");
    }

  }

  private void checkSshKeyNotExists(IamSshKey sshKey) {

    /* Generate fingerprint if null */
    if (sshKey.getFingerprint() == null && sshKey.getValue() != null) {

      try {
        sshKey.setFingerprint(RSAPublicKeyUtils.getSHA256Fingerprint(sshKey.getValue()));
      } catch (InvalidSshKeyException e) {
        throw new ScimException(e.getMessage());
      }
    }

    if (sshKeyRepository.findByFingerprint(sshKey.getFingerprint()).isPresent()) {

      throw new ScimResourceExistsException(
          "ssh key " + sshKey.getFingerprint() + " is already mapped to another user");
    }

  }

  @Override
  public ScimListResponse<ScimUser> list(ScimPageRequest params) {

    if (params.getCount() == 0) {
      int userCount = accountRepository.countAllUsers();
      return new ScimListResponse<>(Collections.emptyList(), userCount, 0, 1);
    }

    OffsetPageable op = new OffsetPageable(params.getStartIndex(), params.getCount());

    Page<IamAccount> results = accountRepository.findAll(op);

    List<ScimUser> resources = new ArrayList<>();

    results.getContent().forEach(a -> resources.add(converter.toScim(a)));

    return new ScimListResponse<>(resources, results.getTotalElements(), resources.size(),
        op.getOffset() + 1);
  }

  @Override
  public ScimUser replace(String id, ScimUser scimItemToBeUpdated) {

    IamAccount existingAccount = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    if (accountRepository.findByUsernameWithDifferentId(scimItemToBeUpdated.getUserName(), id)
      .isPresent()) {
      throw new IllegalArgumentException("userName is already mappped to another user");
    }

    IamAccount updatedAccount = converter.fromScim(scimItemToBeUpdated);

    updatedAccount.setId(existingAccount.getId());
    updatedAccount.setUuid(existingAccount.getUuid());
    updatedAccount.setCreationTime(existingAccount.getCreationTime());

    // If the active field was not provided in the input scim user,
    // use the value that was formerly set in the database
    if (scimItemToBeUpdated.getActive() == null) {
      updatedAccount.setActive(existingAccount.isActive());
    }

    if (scimItemToBeUpdated.getPassword() != null) {

    }

    updatedAccount.touch();

    accountRepository.save(updatedAccount);
    return converter.toScim(updatedAccount);

  }

  @Override
  public void update(String id, List<ScimPatchOperation<ScimUser>> operations) {

    IamAccount iamAccount = accountRepository.findByUuid(id)
      .orElseThrow(() -> new ScimResourceNotFoundException("No user mapped to id '" + id + "'"));

    updater.update(iamAccount, operations);

  }

}
