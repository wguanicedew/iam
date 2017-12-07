package it.infn.mw.iam.api.aup.model;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.controller.utils.JsonDateSerializer;

public class AupSignatureDTO {

  AupDTO aup;
  
  AccountDTO account;
  
  @JsonSerialize(using = JsonDateSerializer.class)
  Date signatureTime;
  
  public AupSignatureDTO() {
    // empty constructor
  }


  public AupDTO getAup() {
    return aup;
  }


  public void setAup(AupDTO aup) {
    this.aup = aup;
  }


  public AccountDTO getAccount() {
    return account;
  }


  public void setAccount(AccountDTO account) {
    this.account = account;
  }
  
  public void setSignatureTime(Date signatureTime) {
    this.signatureTime = signatureTime;
  }
  
}
