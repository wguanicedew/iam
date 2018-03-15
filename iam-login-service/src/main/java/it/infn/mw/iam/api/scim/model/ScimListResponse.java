package it.infn.mw.iam.api.scim.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import it.infn.mw.iam.api.common.ListResponseDTO;

public class ScimListResponse<T> extends ListResponseDTO<T> {

  public static final String SCHEMA = "urn:ietf:params:scim:api:messages:2.0:ListResponse";

  private final Set<String> schemas = new HashSet<>(Collections.singletonList(SCHEMA));

  public ScimListResponse(List<T> resources, long totalResults, int startIndex, int itemsPerPage) {
    super(resources, totalResults, itemsPerPage, startIndex);
  }

  public Set<String> getSchemas() {

    return schemas;
  }

}
