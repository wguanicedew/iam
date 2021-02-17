package it.infn.mw.iam.api.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.infn.mw.iam.api.scim.converter.Converter;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimListResponse.ScimListResponseBuilder;

public class FindUtils {

  public static <D, E> ScimListResponse<D> responseFromPage(Page<E> results,
      Converter<D, E> converter,
      Pageable pageable) {
    ScimListResponseBuilder<D> builder = ScimListResponse.builder();
    List<D> resources = new ArrayList<>();

    results.getContent().forEach(a -> resources.add(converter.dtoFromEntity(a)));

    builder.resources(resources);
    builder.fromPage(results, pageable);

    return builder.build();
  }

}
