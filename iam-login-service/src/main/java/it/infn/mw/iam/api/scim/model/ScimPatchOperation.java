package it.infn.mw.iam.api.scim.model;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.infn.mw.iam.api.scim.exception.IllegalArgumentException;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimPatchOperation<T> {

  public static enum ScimPatchOperationType {
	add, remove, replace
  }

  private final ScimPatchOperationType op;
  @NotBlank
  private final String path;
  private final T value;
  
  @JsonCreator
  private ScimPatchOperation(
	@JsonProperty("op") ScimPatchOperationType op,
	@JsonProperty("path") String path,
	@JsonProperty("value") T value) {
	
	this.op = op;
	this.path = path;
	this.value = value;
  }
  
  public ScimPatchOperation(Builder<T> builder) {
	
	this.op = builder.op;
	if (builder.path == null) {
	  throw new IllegalArgumentException(
		"scim patch operation path cannot be null");
	}
	this.path = builder.path;
	this.value = builder.value;
  }

  public ScimPatchOperationType getOp() {
	
	return op;
  }

  public T getValue() {
	
	return value;
  }

  public String getPath() {
	
	return path;
  }

  public static class Builder<T> {

	ScimPatchOperationType op;
	String path;
	T value;

	public Builder() {
	}

	public Builder<T> op(ScimPatchOperationType op) {

	  this.op = op;
	  return this;
	}

	public Builder<T> path(String path) {

	  this.path = path;
	  return this;
	}

	public Builder<T> value(T val) {

	  this.value = val;
	  return this;
	}
	
	public ScimPatchOperation<T> build() {

	  return new ScimPatchOperation<T>(this);
	}
  }

}
