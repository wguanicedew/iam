package it.infn.mw.iam.api.account.search.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.infn.mw.iam.persistence.model.IamAccount;

public class IamAccountDTO {

  private Long id;
  private String uuid;
  private String username;
  private boolean active;
  private Date creationTime;
  private Date lastUpdateTime;
  private Date lastLoginTime;
  private IamUserInfoDTO userInfo;
  private Set<IamAuthorityDTO> authorities = new HashSet<>();
  private Set<IamGroupDTO> groups = new HashSet<>();

  @JsonCreator
  public IamAccountDTO(@JsonProperty("id") Long id, @JsonProperty("uuid") String uuid,
      @JsonProperty("username") String username, @JsonProperty("active") boolean active,
      @JsonProperty("creationTime") Date creationTime,
      @JsonProperty("lastUpdateTime") Date lastUpdateTime,
      @JsonProperty("lastLoginTime") Date lastLoginTime,
      @JsonProperty("userInfo") IamUserInfoDTO userInfo,
      @JsonProperty("authorities") Set<IamAuthorityDTO> authorities,
      @JsonProperty("groups") Set<IamGroupDTO> groups) {

    this.id = id;
    this.uuid = uuid;
    this.username = username;
    this.active = active;
    this.creationTime = creationTime;
    this.lastUpdateTime = lastUpdateTime;
    this.lastLoginTime = lastLoginTime;
    this.userInfo = userInfo;
    this.authorities = authorities;
    this.groups = groups;
  }

  public IamAccountDTO(Builder builder) {

    this.id = builder.id;
    this.uuid = builder.uuid;
    this.username = builder.username;
    this.active = builder.active;
    this.creationTime = builder.creationTime;
    this.lastUpdateTime = builder.lastUpdateTime;
    this.lastLoginTime = builder.lastLoginTime;
    this.userInfo = builder.userInfo;
    this.authorities = builder.authorities;
    this.groups = builder.groups;
  }

  public Long getId() {
    return id;
  }

  public String getUuid() {
    return uuid;
  }

  public String getUsername() {
    return username;
  }

  public boolean isActive() {
    return active;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public Date getLastLoginTime() {
    return lastLoginTime;
  }

  public IamUserInfoDTO getUserInfo() {
    return userInfo;
  }

  public Set<IamAuthorityDTO> getAuthorities() {
    return authorities;
  }

  public Set<IamGroupDTO> getGroups() {
    return groups;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
    result = prime * result + ((creationTime == null) ? 0 : creationTime.hashCode());
    result = prime * result + ((groups == null) ? 0 : groups.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((lastLoginTime == null) ? 0 : lastLoginTime.hashCode());
    result = prime * result + ((lastUpdateTime == null) ? 0 : lastUpdateTime.hashCode());
    result = prime * result + ((userInfo == null) ? 0 : userInfo.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamAccountDTO other = (IamAccountDTO) obj;
    if (active != other.active)
      return false;
    if (authorities == null) {
      if (other.authorities != null)
        return false;
    } else if (!authorities.equals(other.authorities))
      return false;
    if (creationTime == null) {
      if (other.creationTime != null)
        return false;
    } else if (!creationTime.equals(other.creationTime))
      return false;
    if (groups == null) {
      if (other.groups != null)
        return false;
    } else if (!groups.equals(other.groups))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (lastLoginTime == null) {
      if (other.lastLoginTime != null)
        return false;
    } else if (!lastLoginTime.equals(other.lastLoginTime))
      return false;
    if (lastUpdateTime == null) {
      if (other.lastUpdateTime != null)
        return false;
    } else if (!lastUpdateTime.equals(other.lastUpdateTime))
      return false;
    if (userInfo == null) {
      if (other.userInfo != null)
        return false;
    } else if (!userInfo.equals(other.userInfo))
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String uuid;
    private String username;
    private boolean active;
    private Date creationTime;
    private Date lastUpdateTime;
    private IamUserInfoDTO userInfo;
    private Date lastLoginTime;
    private Set<IamAuthorityDTO> authorities = new HashSet<>();
    private Set<IamGroupDTO> groups = new HashSet<>();

    public Builder fromIamAccount(IamAccount a) {
      this.id = a.getId();
      this.uuid = a.getUuid();
      this.username = a.getUsername();
      this.active = a.isActive();
      this.creationTime = a.getCreationTime();
      this.lastUpdateTime = a.getLastUpdateTime();
      this.lastLoginTime = a.getLastLoginTime();
      this.userInfo = IamUserInfoDTO.builder().fromIamUserInfo(a.getUserInfo()).build();
      a.getAuthorities().forEach(authority -> this.authorities
          .add(IamAuthorityDTO.builder().fromIamAuthority(authority).build()));
      a.getGroups()
          .forEach(group -> this.groups.add(IamGroupDTO.builder().fromIamGroup(group).build()));
      return this;
    }

    public IamAccountDTO build() {

      return new IamAccountDTO(this);
    }
  }
}
