package it.infn.mw.iam.api.account.find;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimListResponse.ScimListResponseBuilder;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Transactional
@Service
public class DefaultFindAccountService implements FindAccountService {

  private final IamAccountRepository repo;
  private final UserConverter converter;

  @Autowired
  public DefaultFindAccountService(IamAccountRepository repo, UserConverter converter) {
    this.repo = repo;
    this.converter = converter;
  }

  private ScimListResponse<ScimUser> responseFromPage(Page<IamAccount> results, Pageable pageable) {
    ScimListResponseBuilder<ScimUser> builder = ScimListResponse.builder();
    List<ScimUser> resources = new ArrayList<>();

    results.getContent().forEach(a -> resources.add(converter.dtoFromEntity(a)));

    builder.resources(resources);
    builder.fromPage(results, pageable);

    return builder.build();
  }

  @Override
  public ScimListResponse<ScimUser> findAccountByLabel(String labelName, String labelValue,
      Pageable pageable) {

    Page<IamAccount> results = repo.findByLabelNameAndValue(labelName, labelValue, pageable);
    return responseFromPage(results, pageable);

  }


  @Override
  public ScimListResponse<ScimUser> findAccountByEmail(String emailAddress) {
    Optional<IamAccount> account = repo.findByEmail(emailAddress);

    ScimListResponseBuilder<ScimUser> builder = ScimListResponse.builder();
    account.ifPresent(a -> builder.singleResource(converter.dtoFromEntity(a)));
    return builder.build();
  }

  @Override
  public ScimListResponse<ScimUser> findAccountByUsername(String username) {
    Optional<IamAccount> account = repo.findByUsername(username);
    ScimListResponseBuilder<ScimUser> builder = ScimListResponse.builder();
    account.ifPresent(a -> builder.singleResource(converter.dtoFromEntity(a)));
    return builder.build();
  }

  @Override
  public ScimListResponse<ScimUser> findInactiveAccounts(Pageable pageable) {

    Page<IamAccount> results = repo.findInactiveAccounts(pageable);
    return responseFromPage(results, pageable);
  }

  @Override
  public ScimListResponse<ScimUser> findActiveAccounts(Pageable pageable) {

    Page<IamAccount> results = repo.findActiveAccounts(pageable);
    return responseFromPage(results, pageable);

  }

}
