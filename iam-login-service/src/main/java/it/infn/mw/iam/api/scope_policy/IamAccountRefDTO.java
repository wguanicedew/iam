package it.infn.mw.iam.api.scope_policy;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import it.infn.mw.iam.api.scope_policy.validation.IamAccountId;

public class IamAccountRefDTO {

  @NotEmpty(message="Invalid scope policy: the account uuid must be a valid UUID")
  @IamAccountId(message="Invalid scope policy: no IAM account found for the given UUID")
  @Size(max=36, message="Invalid scope policy: the UUID is at most 36 chars long")
  String uuid;
  
  String username;
  String location;

  public IamAccountRefDTO() {
    // empty
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
