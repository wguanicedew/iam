package it.infn.mw.iam.registration;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RegistrationRequestDto {

  private String uuid;
  private Date creationTime;
  private String status;
  private String approver;
  private Date lastUpdateTime;
  private String username;
  private String password;
  private String givenname;
  private String familyname;
  private String email;
  private String birthdate;
  private String accountId;

  public RegistrationRequestDto() {}

  @JsonCreator
  public RegistrationRequestDto(@JsonProperty("uuid") final String uuid,
      @JsonProperty("creationdate") final Date creationTime,
      @JsonProperty("status") final String status, @JsonProperty("approver") final String approver,
      @JsonProperty("lastupdatetime") final Date lastUpdateTime,
      @JsonProperty("username") final String username,
      @JsonProperty("password") final String password,
      @JsonProperty("givename") final String givenname,
      @JsonProperty("familyname") final String familyname,
      @JsonProperty("email") final String email, @JsonProperty("birthdate") final String birthdate,
      @JsonProperty("accountid") final String accountId) {
    super();
    this.username = username;
    this.password = password;
    this.givenname = givenname;
    this.familyname = familyname;
    this.email = email;
    this.birthdate = birthdate;
    this.uuid = uuid;
    this.creationTime = creationTime;
    this.status = status;
    this.approver = approver;
    this.lastUpdateTime = lastUpdateTime;
    this.accountId = accountId;
  }

  public String getUuid() {

    return uuid;
  }

  public void setUuid(final String uuid) {

    this.uuid = uuid;
  }

  public Date getCreationTime() {

    return creationTime;
  }

  public void setCreationTime(final Date creationTime) {

    this.creationTime = creationTime;
  }

  public String getStatus() {

    return status;
  }

  public void setStatus(final String status) {

    this.status = status;
  }

  public String getApprover() {

    return approver;
  }

  public void setApprover(final String approver) {

    this.approver = approver;
  }

  public Date getLastUpdateTime() {

    return lastUpdateTime;
  }

  public void setLastUpdateTime(final Date lastUpdateTime) {

    this.lastUpdateTime = lastUpdateTime;
  }

  public String getUsername() {

    return username;
  }

  public void setUsername(final String username) {

    this.username = username;
  }

  public String getPassword() {

    return password;
  }

  public void setPassword(final String password) {

    this.password = password;
  }

  public String getGivenname() {

    return givenname;
  }

  public void setGivenname(final String givenname) {

    this.givenname = givenname;
  }

  public String getFamilyname() {

    return familyname;
  }

  public void setFamilyname(final String familyname) {

    this.familyname = familyname;
  }

  public String getEmail() {

    return email;
  }

  public void setEmail(final String email) {

    this.email = email;
  }

  public String getBirthdate() {

    return birthdate;
  }

  public void setBirthdate(final String birthdate) {

    this.birthdate = birthdate;
  }

  public String getAccountId() {

    return accountId;
  }

  public void setAccountId(final String accountId) {

    this.accountId = accountId;
  }

}
