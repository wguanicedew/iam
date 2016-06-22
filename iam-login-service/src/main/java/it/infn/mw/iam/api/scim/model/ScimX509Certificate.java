package it.infn.mw.iam.api.scim.model;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ScimX509Certificate {

  private final String display;
  private final Boolean primary;

  @NotBlank
  private final String value;

  @JsonIgnore
  private ScimMemberRef accountRef;

  @JsonCreator
  private ScimX509Certificate(@JsonProperty("display") String display,
      @JsonProperty("primary") Boolean primary, @JsonProperty("value") String value) {

    this.display = display;
    this.value = value;
    this.primary = primary;
  }

  public String getDisplay() {

    return display;
  }

  public String getValue() {

    return value;
  }

  public Boolean isPrimary() {

    return primary;
  }

  @JsonIgnore
  public ScimMemberRef getAccountRef() {

    return accountRef;
  }

  private ScimX509Certificate(Builder b) {

    this.display = b.display;
    this.value = b.value;
    this.accountRef = b.accountRef;
    this.primary = b.primary;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private String display;
    private String value;
    private Boolean primary;
    private ScimMemberRef accountRef;

    public Builder() {

    }

    public Builder display(String display) {

      this.display = display;
      return this;
    }

    public Builder value(String value) {

      this.value = value;
      return this;
    }

    public Builder primary(Boolean primary) {

      this.primary = primary;
      return this;
    }

    public Builder accountRef(ScimMemberRef accountRef) {

      this.accountRef = accountRef;
      return this;
    }

    public ScimX509Certificate build() {

      return new ScimX509Certificate(this);
    }
  }
}
