package it.infn.mw.iam.api.account.search;

import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;

public abstract class AbstractSearchController<T> {

  public static final int DEFAULT_ITEMS_PER_PAGE = 10;

  public abstract ListResponseDTO<T> list(int startIndex, int count, String filter);

  protected OffsetPageable getOffsetPageable(int startIndex, int count) {

    if (count < 0) {
      count = 0;
    } else if (count > DEFAULT_ITEMS_PER_PAGE) {
      count = DEFAULT_ITEMS_PER_PAGE;
    }
    if (startIndex <= 0) {
      startIndex = 1;
    }
    return new OffsetPageable(startIndex - 1, count);
  }
}
