package it.infn.mw.iam.api.common;

public class PagingUtils {

  public static OffsetPageable buildPageRequest(Integer count, Integer startIndex,
      int maxPageSize) {

    int validCount = 0;
    int validStartIndex = 1;

    if (count == null) {
      validCount = maxPageSize;
    } else {
      validCount = count;
      if (count < 0) {
        validCount = 0;
      } else if (count > maxPageSize) {
        validCount = maxPageSize;
      }
    }

    if (startIndex == null) {
      validStartIndex = 1;

    } else {

      validStartIndex = startIndex;
      if (startIndex < 0) {
        validStartIndex = 1;
      }
    }

    return new OffsetPageable(validStartIndex - 1, validCount);
  }
}
