package it.infn.mw.iam.api.account.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import it.infn.mw.iam.api.account.search.service.AccountSearchService;
import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.persistence.model.IamAccount;

@RestController
@Transactional
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping(AccountSearchController.ACCOUNT_SEARCH_ENDPOINT)
public class AccountSearchController {

  public static final String ACCOUNT_SEARCH_ENDPOINT = "/iam/account/search";
  public static final int ITEMS_PER_PAGE = 10;

  @Autowired
  private AccountSearchService accountService;

  @PreAuthorize("hasRole('USER')")
  @RequestMapping(method = RequestMethod.GET)
  public ListResponseDTO<IamAccount> getAccounts(
      @RequestParam(required = false, defaultValue = "") String filter,
      @RequestParam(required = false, defaultValue = "1") int startIndex,
      @RequestParam(required = false, defaultValue = "10") int count) {

    Page<IamAccount> accounts;

    if (filter.isEmpty()) {

      accounts = accountService.getAccounts(startIndex, count);

    } else {

      accounts = accountService.getAccounts(startIndex, count, filter);
    }

    ListResponseDTO.Builder<IamAccount> builder = ListResponseDTO.builder();
    builder.fromPage(accounts);
    return builder.build();
  }

}
