package it.infn.mw.iam.api.scim.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScimListResponse<T> {

  public static final String SCHEMA = "urn:ietf:params:scim:api:messages:2.0:ListResponse";

  private long totalResults;
  private long itemsPerPage;
  private long startIndex;

  private Set<String> schemas = new HashSet<>(
    Collections.singletonList(SCHEMA));
  private List<T> resources = new ArrayList<>();

  ScimListResponse() {
  }

  public ScimListResponse(List<T> resources, long totalResults,
    long itemsPerPage, long startIndex) {
    this.resources = resources;
    this.totalResults = totalResults;
    this.itemsPerPage = itemsPerPage;
    this.startIndex = startIndex;
  }

  public long getTotalResults() {

    return totalResults;
  }

  public long getItemsPerPage() {

    return itemsPerPage;
  }

  public long getStartIndex() {

    return startIndex;
  }

  public Set<String> getSchemas() {

    return schemas;
  }

  @JsonProperty("Resources")
  public List<T> getResources() {

    return resources;
  }
  
}
