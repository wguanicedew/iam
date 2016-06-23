package it.infn.mw.iam.api.scim.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class ScimResource {

  private final String id;
  private final String externalId;
  private final ScimMeta meta;

  @JsonProperty(required = true)
  private final Set<String> schemas;

  protected ScimResource(@JsonProperty("id") String id,
      @JsonProperty("externalId") String externalId, @JsonProperty("meta") ScimMeta meta,
      @JsonProperty("schemas") Set<String> schemas) {

    this.id = id;
    this.externalId = externalId;
    this.meta = meta;
    this.schemas = schemas;

  }

  protected ScimResource(Builder<?> b) {
    id = b.id;
    externalId = b.externalid;
    meta = b.meta;
    schemas = b.schemas;
  }

  public String getId() {

    return id;
  }

  public String getExternalId() {

    return externalId;
  }

  public ScimMeta getMeta() {

    return meta;
  }

  public Set<String> getSchemas() {

    return schemas;
  }

  public static abstract class Builder<T extends ScimResource> {

    protected String externalid;
    protected String id;
    protected ScimMeta meta;
    protected Set<String> schemas = new HashSet<>();

    public abstract T build();

  }
}
