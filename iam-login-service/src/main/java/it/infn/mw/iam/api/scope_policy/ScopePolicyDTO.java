package it.infn.mw.iam.api.scope_policy;

import java.util.Date;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cz.jirutka.validator.collection.constraints.EachSize;
import it.infn.mw.iam.api.scim.controller.utils.JsonDateSerializer;
import it.infn.mw.iam.api.scope_policy.validation.ScopePolicy;

@ScopePolicy
public class ScopePolicyDTO {

  private Long id;

  @Max(value = 512, message = "Invalid scope policy: The description string must be at most 512 characters long")
  private String description;

  @JsonSerialize(using = JsonDateSerializer.class)
  private Date creationTime;

  @JsonSerialize(using = JsonDateSerializer.class)
  private Date lastUpdateTime;

  @Pattern(regexp = "PERMIT|DENY", message = "Invalid scope policy: allowed values for 'rule' are: 'PERMIT', 'DENY'")
  private String rule;

  @Valid
  private IamAccountRefDTO account;

  @Valid
  private IamGroupRefDTO group;

  @EachSize(min=1, max=255, message="Invalid scope policy: scope length must be >= 1 and < 255 characters")
  private Set<String> scopes;

  public ScopePolicyDTO() {}

  @JsonCreator
  public ScopePolicyDTO(@JsonProperty("id") long id, 
      @JsonProperty("description") String description, 
      @JsonProperty("creationTime") Date creationTime, 
      @JsonProperty("lastUpdateTime") Date lastUpdateTime,
      @JsonProperty("rule") String rule, 
      @JsonProperty("account") IamAccountRefDTO account, 
      @JsonProperty("group")IamGroupRefDTO group, 
      @JsonProperty("scopes")Set<String> scopes) {
    this.id = id;
    this.description = description;
    this.creationTime = creationTime;
    this.lastUpdateTime = lastUpdateTime;
    this.rule = rule;
    this.account = account;
    this.group = group;
    this.scopes = scopes;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public IamAccountRefDTO getAccount() {
    return account;
  }

  public void setAccount(IamAccountRefDTO account) {
    this.account = account;
  }

  public IamGroupRefDTO getGroup() {
    return group;
  }

  public void setGroup(IamGroupRefDTO group) {
    this.group = group;
  }

  public Set<String> getScopes() {
    return scopes;
  }

  public void setScopes(Set<String> scopes) {
    this.scopes = scopes;
  }

}
