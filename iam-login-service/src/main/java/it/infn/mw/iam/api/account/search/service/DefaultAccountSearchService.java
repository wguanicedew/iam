package it.infn.mw.iam.api.account.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import it.infn.mw.iam.api.scim.provisioning.paging.OffsetPageable;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class DefaultAccountSearchService implements AccountSearchService {

  @Autowired
  private IamAccountRepository accountRepository;

  @Override
  public Page<IamAccount> getAccounts(int startIndex, int count) {

    OffsetPageable op = new OffsetPageable(startIndex, count);
    return accountRepository.findAll(op);
  }

  @Override
  public long getTotalAccounts() {

    return accountRepository.count();
  }

  @Override
  public Page<IamAccount> getAccounts(int startIndex, int count, String filter) {

    OffsetPageable op = new OffsetPageable(startIndex, count);
    return accountRepository.findByFilter(filter, op);
  }

  @Override
  public long getTotalAccounts(String filter) {
    return accountRepository.countByFilter(filter);
  }

}
