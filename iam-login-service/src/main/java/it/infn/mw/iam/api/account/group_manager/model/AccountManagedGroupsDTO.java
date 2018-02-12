package it.infn.mw.iam.api.account.group_manager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AccountManagedGroupsDTO {

  final String id;
  final String username;
  
  final List<GroupDTO> managedGroups;
  final List<GroupDTO> unmanagedGroups;

  private AccountManagedGroupsDTO(Builder builder) {
    this.id = builder.id;
    this.username = builder.username;
    this.managedGroups = Collections.unmodifiableList(builder.managedGroups);
    this.unmanagedGroups = Collections.unmodifiableList(builder.unmanagedGroups);
  }
  
  public String getId() {
    return id;
  }
  
  
  public String getUsername() {
    return username;
  }

  public List<GroupDTO> getManagedGroups() {
    return managedGroups;
  }


  public List<GroupDTO> getUnmanagedGroups() {
    return unmanagedGroups;
  }


  public static class Builder {
    String username;
    String id;
    List<GroupDTO> managedGroups = new ArrayList<>();
    List<GroupDTO> unmanagedGroups = new ArrayList<>();

    public Builder() {
      // empty constructor
    }

    public Builder username(String username) {
      this.username = username;
      return this;
    }

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder managedGroups(List<GroupDTO> managedGroups) {
      this.managedGroups = managedGroups;
      return this;
    }

    public Builder unmanagedGroups(List<GroupDTO> unmanagedGroups) {
      this.unmanagedGroups = unmanagedGroups;
      return this;
    }

    public AccountManagedGroupsDTO build() {
      return new AccountManagedGroupsDTO(this);
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }
}
