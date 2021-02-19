package it.infn.mw.iam.api.scim.model;

import javax.annotation.Generated;

public class ScimLabel {

  private final String prefix;
  private final String name;
  private final String value;

  public String getPrefix() {
    return prefix;
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  private ScimLabel(Builder builder) {
    this.prefix = builder.prefix;
    this.name = builder.name;
    this.value = builder.value;
  }

  @Override
  @Generated("eclipse")
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  @Generated("eclipse")
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScimLabel other = (ScimLabel) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (prefix == null) {
      if (other.prefix != null)
        return false;
    } else if (!prefix.equals(other.prefix))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String prefix;
    private String name;
    private String value;

    public Builder withPrefix(String prefix) {
      this.prefix = prefix;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withVaule(String value) {
      this.value = value;
      return this;
    }

    public ScimLabel build() {
      return new ScimLabel(this);
    }
  }
}
