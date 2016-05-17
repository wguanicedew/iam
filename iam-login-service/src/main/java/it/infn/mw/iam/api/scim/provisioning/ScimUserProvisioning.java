package it.infn.mw.iam.api.scim.provisioning;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;
import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRespository;

@Service
public class ScimUserProvisioning implements ScimProvisioning<ScimUser> {

  private final UserConverter converter;

  private final IamAccountRespository accountRepository;

  @Autowired
  public ScimUserProvisioning(UserConverter converter,
    IamAccountRespository repo) {
    this.converter = converter;
    this.accountRepository = repo;
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

    Optional<IamAccount> account = accountRepository.findByUuid(id);

    if (account.isPresent()) {
      return converter.toScim(account.get());
    }

    throw new ResourceNotFoundException("No user mapped to id '" + id + "'");

  }

  @Override
  public void delete(String id) {

    accountRepository.findByUuid(id).ifPresent(a -> {
      accountRepository.delete(a);
    });

  }

}
