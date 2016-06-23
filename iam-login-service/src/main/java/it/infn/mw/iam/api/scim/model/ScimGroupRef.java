package it.infn.mw.iam.api.scim.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimGroupRef {

  private final String value;
  private final String display;
  private final String ref;

  @JsonCreator
  private ScimGroupRef(@JsonProperty("display") String display, @JsonProperty("value") String value,
      @JsonProperty("$ref") String ref) {

    this.display = display;
    this.value = value;
    this.ref = ref;
  }

  public ScimGroupRef(Builder b) {

    value = b.value;
    display = b.display;
    ref = b.ref;
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

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

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
