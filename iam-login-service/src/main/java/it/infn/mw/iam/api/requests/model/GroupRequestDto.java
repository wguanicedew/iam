package it.infn.mw.iam.api.requests.model;

import java.util.Date;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import it.infn.mw.iam.api.requests.validator.GroupRequest;
import it.infn.mw.iam.api.validators.IamGroupName;
import it.infn.mw.iam.api.validators.IamGroupRequestNotes;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@GroupRequest
public class GroupRequestDto {

  private String uuid;

  private String username;

  @NotEmpty
  @IamGroupName(message = "Invalid membership request: group does not exist")
  private String groupName;

  private String status;

  @NotEmpty
  @IamGroupRequestNotes(message = "Invalid membership request: notes cannot be empty")
  private String notes;

  private String motivation;
  private Date creationTime;
  private Date lastUpdateTime;

  public GroupRequestDto() {
    // empty constructor
  }

  @JsonCreator
  public GroupRequestDto(@JsonProperty("uuid") String uuid,
      @JsonProperty("username") String username, @JsonProperty("group_name") String groupName,
      @JsonProperty("status") String status, @JsonProperty("notes") String notes,
      @JsonProperty("motivation") String motivation,
      @JsonProperty("creation_time") Date creationTime,
      @JsonProperty("last_update_time") Date lastUpdateTime) {
    super();
    this.uuid = uuid;
    this.username = username;
    this.groupName = groupName;
    this.status = status;
    this.notes = notes;
    this.motivation = motivation;
    this.creationTime = creationTime;
    this.lastUpdateTime = lastUpdateTime;
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

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getMotivation() {
    return motivation;
  }

  public void setMotivation(String motivation) {
    this.motivation = motivation;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Date lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }
}
