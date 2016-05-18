package it.infn.mw.iam.api.scim.provisioning;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRespository;
import it.infn.mw.iam.persistence.repository.IamAuthoritiesRepository;

@Service
public class ScimUserProvisioning implements ScimProvisioning<ScimUser> {

  private final UserConverter converter;

  private final IamAccountRespository accountRepository;

  private final IamAuthoritiesRepository authorityRepository;

  @Autowired
  public ScimUserProvisioning(UserConverter converter,
    IamAccountRespository accountRepo, IamAuthoritiesRepository authorityRepo) {
    this.converter = converter;
    this.accountRepository = accountRepo;
    this.authorityRepository = authorityRepo;

  }

  private void idSanityChecks(String id) {

    if (id == null) {
      throw new IllegalArgumentException("id cannot be null");
    }

    if (id.trim()
      .isEmpty()) {
      throw new IllegalArgumentException("id cannot be the empty string");
    }
  }

  @Override
  public ScimUser getById(String id) {

    idSanityChecks(id);

    Optional<IamAccount> account = accountRepository.findByUuid(id);

    if (account.isPresent()) {
      return converter.toScim(account.get());
    }

    throw new ResourceNotFoundException("No user mapped to id '" + id + "'");

  }

  @Override
  public void delete(String id) {

    accountRepository.findByUuid(id)
      .ifPresent(a -> {
        accountRepository.delete(a);
      });

  }

  @Override
  public ScimUser create(ScimUser user) {

    IamAccount account = new IamAccount();

    Date creationTime = new Date();
    String uuid = UUID.randomUUID()
      .toString();

    account.setUuid(uuid);
    account.setCreationTime(creationTime);
    account.setLastUpdateTime(creationTime);
    account.setUsername(user.getUserName());
    account.setActive(true);

    authorityRepository.findByAuthority("ROLE_USER")
      .map(a -> account.getAuthorities()
        .add(a))
      .orElseThrow(() -> new IllegalStateException(
        "ROLE_USER not found in database. This is a bug"));

    IamUserInfo userInfo = new IamUserInfo();
    userInfo.setGivenName(user.getName()
      .getGivenName());
    userInfo.setFamilyName(user.getName()
      .getFamilyName());

    userInfo.setEmail(user.getEmails()
      .get(0)
      .getValue());
    account.setUserInfo(userInfo);

    accountRepository.save(account);

    return converter.toScim(account);
  }

}
