/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
