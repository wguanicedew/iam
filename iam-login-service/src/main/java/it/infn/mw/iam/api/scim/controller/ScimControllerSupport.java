package it.infn.mw.iam.api.scim.controller;

import it.infn.mw.iam.api.scim.provisioning.paging.DefaultScimPageRequest;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;

public class ScimControllerSupport {

  protected static final int SCIM_USER_MAX_PAGE_SIZE = 100;
  protected static final int SCIM_GROUP_MAX_PAGE_SIZE = 10;

  protected ScimPageRequest buildUserPageRequest(Integer count, Integer startIndex) {
    return buildPageRequest(count, startIndex, SCIM_USER_MAX_PAGE_SIZE);
  }

  protected ScimPageRequest buildGroupPageRequest(Integer count, Integer startIndex) {
    return buildPageRequest(count, startIndex, SCIM_GROUP_MAX_PAGE_SIZE);
  }

  private ScimPageRequest buildPageRequest(Integer count, Integer startIndex, int maxPageSize) {

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

    // SCIM pages index is 1-based
    if (startIndex == null) {
      validStartIndex = 1;

    } else {

      validStartIndex = startIndex;
      if (startIndex < 1) {
        validStartIndex = 1;
      }
    }

    ScimPageRequest pr = new DefaultScimPageRequest.Builder().count(validCount)
      .startIndex(validStartIndex - 1)
      .build();

    return pr;
  }

}
