package it.infn.mw.iam.api.account.search.model;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.infn.mw.iam.persistence.model.IamUserInfo;

@JsonInclude(NON_NULL)
public class IamUserInfoDTO {

  private Long id;
  private String givenName;
  private String familyName;
  private String middleName;
  private String nickname;
  private String profile;
  private String picture;
  private String email;
  private Boolean emailVerified;

  @JsonCreator
  public IamUserInfoDTO(@JsonProperty("id") Long id, @JsonProperty("givenName") String givenName,
      @JsonProperty("familyName") String familyName, @JsonProperty("middleName") String middleName,
      @JsonProperty("nickname") String nickname, @JsonProperty("profile") String profile,
      @JsonProperty("picture") String picture, @JsonProperty("email") String email,
      @JsonProperty("emailVerified") Boolean emailVerified) {

    this.id = id;
    this.givenName = givenName;
    this.familyName = familyName;
    this.middleName = middleName;
    this.nickname = nickname;
    this.profile = profile;
    this.picture = picture;
    this.email = email;
    this.emailVerified = emailVerified;
  }

  public IamUserInfoDTO(Builder builder) {

    this.id = builder.id;
    this.givenName = builder.givenName;
    this.familyName = builder.familyName;
    this.middleName = builder.middleName;
    this.nickname = builder.nickname;
    this.profile = builder.profile;
    this.picture = builder.picture;
    this.email = builder.email;
    this.emailVerified = builder.emailVerified;
  }

  public Long getId() {
    return id;
  }

  public String getGivenName() {
    return givenName;
  }

  public String getFamilyName() {
    return familyName;
  }

  public String getMiddleName() {
    return middleName;
  }

  public String getNickname() {
    return nickname;
  }

  public String getProfile() {
    return profile;
  }

  public String getPicture() {
    return picture;
  }

  public String getEmail() {
    return email;
  }

  public Boolean getEmailVerified() {
    return emailVerified;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((email == null) ? 0 : email.hashCode());
    result = prime * result + ((emailVerified == null) ? 0 : emailVerified.hashCode());
    result = prime * result + ((familyName == null) ? 0 : familyName.hashCode());
    result = prime * result + ((givenName == null) ? 0 : givenName.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
    result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
    result = prime * result + ((picture == null) ? 0 : picture.hashCode());
    result = prime * result + ((profile == null) ? 0 : profile.hashCode());
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
    IamUserInfoDTO other = (IamUserInfoDTO) obj;
    if (email == null) {
      if (other.email != null)
        return false;
    } else if (!email.equals(other.email))
      return false;
    if (emailVerified == null) {
      if (other.emailVerified != null)
        return false;
    } else if (!emailVerified.equals(other.emailVerified))
      return false;
    if (familyName == null) {
      if (other.familyName != null)
        return false;
    } else if (!familyName.equals(other.familyName))
      return false;
    if (givenName == null) {
      if (other.givenName != null)
        return false;
    } else if (!givenName.equals(other.givenName))
      return false;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    if (middleName == null) {
      if (other.middleName != null)
        return false;
    } else if (!middleName.equals(other.middleName))
      return false;
    if (nickname == null) {
      if (other.nickname != null)
        return false;
    } else if (!nickname.equals(other.nickname))
      return false;
    if (picture == null) {
      if (other.picture != null)
        return false;
    } else if (!picture.equals(other.picture))
      return false;
    if (profile == null) {
      if (other.profile != null)
        return false;
    } else if (!profile.equals(other.profile))
      return false;
    return true;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private Long id;
    private String givenName;
    private String familyName;
    private String middleName;
    private String nickname;
    private String profile;
    private String picture;
    private String email;
    private Boolean emailVerified;

    public Builder fromIamUserInfo(IamUserInfo userInfo) {

      this.id = userInfo.getId();
      this.givenName = userInfo.getGivenName();
      this.familyName = userInfo.getFamilyName();
      this.middleName = userInfo.getMiddleName();
      this.nickname = userInfo.getNickname();
      this.profile = userInfo.getProfile();
      this.picture = userInfo.getPicture();
      this.email = userInfo.getEmail();
      this.emailVerified = userInfo.getEmailVerified();
      return this;
    }

    public IamUserInfoDTO build() {

      return new IamUserInfoDTO(this);
    }
  }
}
