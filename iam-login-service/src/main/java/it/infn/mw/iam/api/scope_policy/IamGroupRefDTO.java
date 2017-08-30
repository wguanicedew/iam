package it.infn.mw.iam.api.scope_policy;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import it.infn.mw.iam.api.scope_policy.validation.IamGroupId;

public class IamGroupRefDTO {
  
  @NotEmpty(message="Invalid scope policy: The iam group uuid must be a valid UUID")
  @Size(max=36, message="Invalid scope policy: the UUID is at most 36 chars long")
  @IamGroupId(message="Invalid scope policy: no IAM group found for the given UUID")
  String uuid;
  
  String name;
  String location;

  public IamGroupRefDTO() {
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
