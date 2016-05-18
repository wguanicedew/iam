package it.infn.mw.iam.api.scim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimGroupRef {

  public static enum ScimGroupRefType {
    direct,
    indirect;
  }

  private final ScimGroupRefType type;

  private final String value;
  private final String display;

  private final String ref;

  public ScimGroupRef(Builder b) {
    type = b.type;
    value = b.value;
    display = b.display;
    ref = b.ref;

  }

  public ScimGroupRefType getType() {

    return type;
  }

  public String getValue() {

    return value;
  }

  public String getDisplay() {

    return display;
  }

  @JsonProperty("$ref")
  public String getRef() {

    return ref;
  }

  public static class Builder {

    private ScimGroupRefType type = ScimGroupRefType.direct;
    private String value;
    private String display;
    private String ref;

    public Builder value(String value) {

      this.value = value;
      return this;
    }

    public Builder display(String display) {

      this.display = display;
      return this;
    }

    public Builder ref(String ref) {

      this.ref = ref;
      return this;
    }

    public ScimGroupRef build() {

      return new ScimGroupRef(this);
    }

  }

}
