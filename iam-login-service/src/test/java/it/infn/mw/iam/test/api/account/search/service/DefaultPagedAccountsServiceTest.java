package it.infn.mw.iam.test.api.account.search.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagedResourceService;
import it.infn.mw.iam.persistence.model.IamAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class DefaultPagedAccountsServiceTest {

  public final int ITEMS_PER_PAGE = 10;
  public final long TOTAL_TEST_ACCOUNTS = 253L;

  @Autowired
  private PagedResourceService<IamAccount> accountService;

  @Test
  public void testGetFirstPageNoFilter() {
    OffsetPageable op = new OffsetPageable(0, ITEMS_PER_PAGE);
    Page<IamAccount> page = accountService.getPage(op);
    assertThat(page.getContent(), hasSize(ITEMS_PER_PAGE));
    assertThat(page.getNumber(), equalTo(0));
    assertThat(page.getNumberOfElements(), equalTo(ITEMS_PER_PAGE));
    assertThat(page.hasNext(), is(true));
    assertThat(page.getTotalElements(), equalTo(TOTAL_TEST_ACCOUNTS));
  }

}
