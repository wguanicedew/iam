package it.infn.mw.iam.api.aup.model;

public class AccountDTO {

  String uuid;

  String username;

  String name;

  public AccountDTO() {
    // private constructor
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
