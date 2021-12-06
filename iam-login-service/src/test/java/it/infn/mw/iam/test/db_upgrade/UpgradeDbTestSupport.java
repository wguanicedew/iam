package it.infn.mw.iam.test.db_upgrade;

import static it.infn.mw.iam.test.api.account.search.service.DefaultPagedAccountsServiceTests.TOTAL_TEST_ACCOUNTS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public abstract class UpgradeDbTestSupport {

  public static final String INITDB_DIR = "/docker-entrypoint-initdb.d";
  public static final String DB_DUMPS_DIR = "db-dumps";

  public static String joinPathStrings(String first, String second) {
    return String.format("%s/%s", first, second);
  }

  @Autowired
  IamAccountRepository accountRepo;

  @Test
  public void dbUpgradeSucceeds() throws IOException {
    assertThat(accountRepo.count(), is(TOTAL_TEST_ACCOUNTS));
  }
}
