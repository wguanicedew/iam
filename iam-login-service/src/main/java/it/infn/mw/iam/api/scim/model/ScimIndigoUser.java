package it.infn.mw.iam.api.scim.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimIndigoUser {

  private final List<ScimSshKey> sshKeys;
  private final List<ScimOidcId> oidcIds;
  private final List<ScimSamlId> samlIds;

  private ScimIndigoUser(Builder b) {
    this.sshKeys = b.sshKeys;
    this.oidcIds = b.oidcIds;
    this.samlIds = b.samlIds;
  }

  public List<ScimSshKey> getSshKeys() {

    return sshKeys;
  }

  public List<ScimOidcId> getOidcIds() {

    return oidcIds;
  }

  public List<ScimSamlId> getSamlIds() {

    return samlIds;
  }

  public static class Builder {

    private List<ScimSshKey> sshKeys = new ArrayList<>();
    private List<ScimOidcId> oidcIds = new ArrayList<>();
    private List<ScimSamlId> samlIds = new ArrayList<>();

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

    public ScimIndigoUser build(){
      return new ScimIndigoUser(this);
    }
  }

}
