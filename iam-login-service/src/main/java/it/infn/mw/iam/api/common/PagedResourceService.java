package it.infn.mw.iam.api.common;

import org.springframework.data.domain.Page;

public interface PagedResourceService<T> {

  Page<T> getPage(OffsetPageable op);

  long count();

  Page<T> getPage(OffsetPageable op, String filter);

  long count(String filter);

}
