package it.infn.mw.iam.api.aup.model;

import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.api.scim.controller.utils.JsonDateSerializer;

public class AupDTO {

  @NotBlank(message = "Invalid AUP: the AUP text cannot be blank")
  String text;

  @Size(max = 128,
      message = "Invalid AUP: the description string must be at most 128 characters long")
  String description;
  
  @NotNull(message = "Invalid AUP: signatureValidityInDays is required")
  @Min(value=0L, message="Invalid AUP: signatureValidityInDays must be >= 0")
  Long signatureValidityInDays;

  @JsonSerialize(using = JsonDateSerializer.class)
  Date creationTime;

  @JsonSerialize(using = JsonDateSerializer.class)
  Date lastUpdateTime;

  public AupDTO(@JsonProperty("text") String text, @JsonProperty("description") String description,
      @JsonProperty("signatureValidityInDays") Long signatureValidityInDays,
      @JsonProperty("creationTime") Date creationTime,
      @JsonProperty("lastUpdateTime") Date lastUpdateTime) {
    this.text = text;
    this.description = description;
    this.signatureValidityInDays = signatureValidityInDays;
    this.creationTime = creationTime;
    this.lastUpdateTime = lastUpdateTime;
  }

  public AupDTO() {
    // empty constructor, useful for testing
  }

  public String getDescription() {
    return description;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public String getText() {
    return text;
  }


  public void setText(String text) {
    this.text = text;
  }


  public Long getSignatureValidityInDays() {
    return signatureValidityInDays;
  }


  public void setSignatureValidityInDays(Long signatureValidityInDays) {
    this.signatureValidityInDays = signatureValidityInDays;
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
