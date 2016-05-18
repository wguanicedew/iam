package it.infn.mw.iam.api.scim.model;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ScimEmail {

  public static enum ScimEmailType {
    work,
    home,
    other;
  }

  private final ScimEmailType type;

  @Email(groups = ScimUser.NewUserValidation.class)
  private final String value;

  private final Boolean primary;

  @JsonCreator
  private ScimEmail(@JsonProperty("type") ScimEmailType type,
    @JsonProperty("value") String value,
    @JsonProperty("primary") Boolean primary) {
    this.type = type;
    this.value = value;
    this.primary = primary;
  }

  private ScimEmail(Builder b) {
    this.type = b.type;
    this.value = b.value;
    this.primary = b.primary;
  }

  public ScimEmailType getType() {

    return type;
  }

  public String getValue() {

    return value;
  }

  public Boolean getPrimary() {

    return primary;
  }

  public static class Builder {

    private ScimEmailType type;
    private String value;
    private Boolean primary;

    public Builder() {
      type = ScimEmailType.work;
      primary = true;
    }

    public Builder email(String value) {

      this.value = value;
      return this;
    }

    public ScimEmail build() {

      return new ScimEmail(this);
    }

  }

}
