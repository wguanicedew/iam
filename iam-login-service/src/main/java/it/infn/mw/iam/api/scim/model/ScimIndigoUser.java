package it.infn.mw.iam.api.scim.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimIndigoUser {

  private final Set<ScimSshKey> sshKeys;
  private final Set<ScimOidcId> oidcIds;
  private final Set<ScimSamlId> samlIds;

  @JsonCreator
  private ScimIndigoUser(@JsonProperty("oidcIds") Set<ScimOidcId> oidcIds,
      @JsonProperty("sshKeys") Set<ScimSshKey> sshKeys,
      @JsonProperty("samlIds") Set<ScimSamlId> samlIds) {

    this.oidcIds = oidcIds != null ? oidcIds : new HashSet<ScimOidcId>();
    this.sshKeys = sshKeys != null ? sshKeys : new HashSet<ScimSshKey>();
    this.samlIds = samlIds != null ? samlIds : new HashSet<ScimSamlId>();

  }

  private ScimIndigoUser(Builder b) {
    this.sshKeys = b.sshKeys;
    this.oidcIds = b.oidcIds;
    this.samlIds = b.samlIds;
  }

  @JsonIgnore
  public boolean isEmpty() {

    return sshKeys.isEmpty() && oidcIds.isEmpty() && samlIds.isEmpty();
  }

  public Set<ScimSshKey> getSshKeys() {

    return sshKeys;
  }

  public Set<ScimOidcId> getOidcIds() {

    return oidcIds;
  }

  public Set<ScimSamlId> getSamlIds() {

    return samlIds;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private Set<ScimSshKey> sshKeys = new HashSet<ScimSshKey>();
    private Set<ScimOidcId> oidcIds = new HashSet<ScimOidcId>();
    private Set<ScimSamlId> samlIds = new HashSet<ScimSamlId>();

    public Builder addSshKey(ScimSshKey sshKey) {

      sshKeys.add(sshKey);
      return this;
    }

    public Builder addOidcid(ScimOidcId oidcId) {

      oidcIds.add(oidcId);
      return this;
    }

    public Builder addSamlId(ScimSamlId samlId) {

      samlIds.add(samlId);
      return this;
    }

    public ScimIndigoUser build() {

      return new ScimIndigoUser(this);
    }
  }

}
