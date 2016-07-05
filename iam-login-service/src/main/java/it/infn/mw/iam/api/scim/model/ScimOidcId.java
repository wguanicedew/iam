package it.infn.mw.iam.api.scim.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimOidcId {

  private final String issuer;
  private final String subject;

  @JsonIgnore
  private ScimMemberRef accountRef;

  @JsonCreator
  private ScimOidcId(@JsonProperty("issuer") String issuer,
      @JsonProperty("subject") String subject) {

    this.issuer = issuer;
    this.subject = subject;
  }

  private ScimOidcId(Builder b) {
    this.issuer = b.issuer;
    this.subject = b.subject;
    this.accountRef = b.accountRef;
  }

  public String getIssuer() {

    return issuer;
  }

  public String getSubject() {

    return subject;
  }

  @JsonIgnore
  public ScimMemberRef getAccountRef() {

    return accountRef;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private String issuer;
    private String subject;
    private ScimMemberRef accountRef;

    public Builder issuer(String issuer) {

      this.issuer = issuer;
      return this;
    }

    public Builder subject(String subject) {

      this.subject = subject;
      return this;
    }

    public Builder accountRef(ScimMemberRef accountRef) {

      this.accountRef = accountRef;
      return this;
    }

    public ScimOidcId build() {

      return new ScimOidcId(this);
    }
  }

  @Override
  public String toString() {
    return "ScimOidcId [issuer=" + issuer + ", subject=" + subject + ", accountRef=" + accountRef
        + "]";
  }
}
