package it.infn.mw.iam.api.scim.model;

public class ScimEmail {

  public static enum ScimEmailType {
    work,
    home,
    other;
  }

  private final ScimEmailType type;
  private final String value;
  private final Boolean primary;

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
