package it.infn.mw.iam.api.account.search.service;

import org.springframework.data.domain.Page;
import it.infn.mw.iam.persistence.model.IamAccount;

/**
 * The IAM Account search service
 *
 */
public interface AccountSearchService {

  Page<IamAccount> getAccounts(int startIndex, int count);

  long getTotalAccounts();

  Page<IamAccount> getAccounts(int startIndex, int count, String filter);

  long getTotalAccounts(String filter);

}
