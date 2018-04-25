package it.infn.mw.iam.api.tokens.service;

import java.util.List;
import it.infn.mw.iam.api.common.ListResponseDTO;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.tokens.service.paging.TokensPageRequest;

public abstract class AbstractTokenService<T> implements TokenService<T> {


  protected OffsetPageable getOffsetPageable(TokensPageRequest pageRequest) {

    if (pageRequest.getCount() == 0) {
      return new OffsetPageable(0, 1);
    }
    return new OffsetPageable(pageRequest.getStartIndex(), pageRequest.getCount());
  }

  protected boolean isCountRequest(TokensPageRequest pageRequest) {

    return pageRequest.getCount() == 0;
  }

  protected ListResponseDTO<T> buildListResponse(List<T> resources, OffsetPageable op, long totalElements) {
    
    ListResponseDTO.Builder<T> builder = ListResponseDTO.builder();
    builder.itemsPerPage(resources.size());
    builder.startIndex(op.getOffset() + 1);
    builder.resources(resources);
    builder.totalResults(totalElements);
    return builder.build();
  }
}
