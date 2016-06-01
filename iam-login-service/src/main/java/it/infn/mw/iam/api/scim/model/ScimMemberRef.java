package it.infn.mw.iam.api.scim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ScimMemberRef {

  public static enum ScimMemberRefType {
	direct, indirect;
  }

  private final ScimMemberRefType type;

  private final String value;
  private final String display;

  private final String ref;

  private ScimMemberRef(Builder builder) {

	type = builder.type;
	value = builder.value;
	display = builder.display;
	ref = builder.ref;
  }

  @JsonProperty("$ref")
  public String getRef() {

	return ref;
  }

  public String getValue() {

	return value;
  }

  public String getDisplay() {

	return display;
  }

  public ScimMemberRefType getType() {

	return type;
  }

  public static class Builder {

	private ScimMemberRefType type = ScimMemberRefType.direct;
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

	public ScimMemberRef build() {

	  return new ScimMemberRef(this);
	}
  }
}