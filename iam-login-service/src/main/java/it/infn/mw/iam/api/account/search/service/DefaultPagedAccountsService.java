package it.infn.mw.iam.api.account.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagedResourceService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class DefaultPagedAccountsService implements PagedResourceService<IamAccount> {

  @Autowired
  private IamAccountRepository accountRepository;

  @Override
  public Page<IamAccount> getPage(OffsetPageable op) {

    return accountRepository.findAll(op);
  }

  @Override
  public long count() {

    return accountRepository.count();
  }

  @Override
  public Page<IamAccount> getPage(OffsetPageable op, String filter) {

    filter = String.format("%%%s%%", filter);
    return accountRepository.findByFilter(filter, op);
  }

  @Override
  public long count(String filter) {

    filter = String.format("%%%s%%", filter);
    return accountRepository.countByFilter(filter);
  }

}
