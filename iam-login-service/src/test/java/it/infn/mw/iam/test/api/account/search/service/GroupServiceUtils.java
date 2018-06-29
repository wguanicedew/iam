package it.infn.mw.iam.test.api.account.search.service;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import java.util.List;
import it.infn.mw.iam.persistence.model.IamGroup;

public class GroupServiceUtils {

  public static void assertSortIsByNameAsc(List<IamGroup> groups) {

    if (groups.size() > 1) {
      for (int i = 1; i < groups.size(); i++) {
        assertThatSortIsByNameAsc(groups.get(i - 1), groups.get(i));
      }
    }
  }

  public static void assertSortIsByNameDesc(List<IamGroup> groups) {

    if (groups.size() > 1) {
      for (int i = 1; i < groups.size(); i++) {
        assertThatSortIsByNameDesc(groups.get(i - 1), groups.get(i));
      }
    }
  }

  public static void assertThatSortIsByNameAsc(IamGroup prior, IamGroup next) {

    assertThat(prior.getName(), lessThanOrEqualTo(next.getName()));
  }

  public static void assertThatSortIsByNameDesc(IamGroup prior, IamGroup next) {

    assertThat(prior.getName(), greaterThanOrEqualTo(next.getName()));
  }
}
