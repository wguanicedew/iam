package it.infn.mw.iam.api.scim.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
public class ScimSamlId {

  @NotBlank
  @Length(max = 256)
  private final String idpId;

  @NotBlank
  @Length(max = 256)
  private final String userId;

  @JsonIgnore
  private ScimMemberRef accountRef;

  @JsonCreator
  private ScimSamlId(@JsonProperty("idpId") String idpId, @JsonProperty("userId") String userId) {

    this.userId = userId;
    this.idpId = idpId;
  }

  public String getUserId() {

    return userId;
  }

  public String getIdpId() {

    return idpId;
  }

  @JsonIgnore
  public ScimMemberRef getAccountRef() {

    return accountRef;
  }

  private ScimSamlId(Builder b) {

    this.idpId = b.idpId;
    this.userId = b.userId;
    this.accountRef = b.accountRef;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private String idpId;
    private String userId;
    private ScimMemberRef accountRef;

    public Builder idpId(String idpId) {

      this.idpId = idpId;
      return this;
    }

    public Builder userId(String userId) {

      this.userId = userId;
      return this;
    }

    public Builder accountRef(ScimMemberRef accountRef) {

      this.accountRef = accountRef;
      return this;
    }

    public ScimSamlId build() {

      return new ScimSamlId(this);
    }
  }
}