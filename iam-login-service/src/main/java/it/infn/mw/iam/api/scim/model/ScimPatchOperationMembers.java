package it.infn.mw.iam.api.scim.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class ScimPatchOperationMembers implements ScimPatchOperation {

  private final ScimPatchOperationType op;
  private final String path;
  private final List<ScimMemberRef> value;

  @JsonCreator
  private ScimPatchOperationMembers(
	@JsonProperty("op") ScimPatchOperationType op,
	@JsonProperty("path") String path,
	@JsonProperty("value") List<ScimMemberRef> value) {

	if (!path.equals("members")) {
	  throw new ScimPatchOperationNotSupported("Expected path: 'members'");
	}

	this.op = op;
	this.path = path;
	this.value = value;
  }

  private ScimPatchOperationMembers(Builder builder) {

	this.op = builder.op;
	this.path = builder.path;
	this.value = builder.value;
  }

  public static Builder builder() {

	return new Builder();
  }

  public static class Builder {

	ScimPatchOperationType op;
	String path;
	List<ScimMemberRef> value;

	public Builder() {
	  
	  path = "members";
	}

	public Builder op(ScimPatchOperationType op) {

	  this.op = op;
	  return this;
	}

	public Builder value(List<ScimMemberRef> val) {

	  this.value = val;
	  return this;
	}

	public ScimPatchOperationMembers build() {

	  return new ScimPatchOperationMembers(this);
	}
  }

  @Override
  public ScimPatchOperationType getOp() {

	return op;
  }

  @Override
  public List<ScimMemberRef> getValue() {

	return value;
  }

  @Override
  public String getPath() {

	return path;
  }

}
