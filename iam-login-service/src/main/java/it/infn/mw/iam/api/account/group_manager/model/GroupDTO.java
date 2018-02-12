package it.infn.mw.iam.api.account.group_manager.model;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GroupDTO {

  final String name;
  
  @NotBlank(message = "Invalid GroupDTO: the Group id cannot be blank")
  final String id;

  private GroupDTO(Builder builder) {
    this.name = builder.name;
    this.id = builder.id;
  }

  public GroupDTO(@JsonProperty("name") String name, @JsonProperty("id") String id) {
    this.name = name;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }
  
  public static class Builder {
    String name;
    String id;

    public Builder() {
      // empty constructor
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public GroupDTO build() {
      return new GroupDTO(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

}
