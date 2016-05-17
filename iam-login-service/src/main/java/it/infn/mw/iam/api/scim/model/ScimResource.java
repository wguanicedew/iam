package it.infn.mw.iam.api.scim.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScimResource {

  private final String id;
  private final String externalId;

  private final ScimMeta meta;

  @JsonProperty(required = true)
  private final Set<String> schemas;

  protected ScimResource(Builder b) {
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

  public static class Builder {

    private String externalid;
    private String id;
    private ScimMeta meta;
    private Set<String> schemas = new HashSet<>();

    protected Builder addSchema(String schema) {

      if (schemas == null) {
        schemas = new HashSet<>();
      }
      schemas.add(schema);

      return this;
    }

    public Builder id(String id) {

      this.id = id;
      return this;
    }

    public Builder externalId(String id) {

      this.externalid = id;
      return this;
    }

    public Builder meta(ScimMeta meta) {

      this.meta = meta;
      return this;
    }

    public ScimResource build() {

      return new ScimResource(this);
    }

  }
}
