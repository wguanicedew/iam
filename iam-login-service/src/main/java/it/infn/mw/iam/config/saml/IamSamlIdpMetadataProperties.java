package it.infn.mw.iam.config.saml;

import static java.lang.Boolean.FALSE;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saml.idp-metadata")
public class IamSamlIdpMetadataProperties {

  @NotBlank
  private String metadataUrl;
  
  private Boolean requireValidSignature = FALSE;
  
  private Boolean requireSirtfi = FALSE;
  
  private Boolean requireRs = FALSE;
  
  private String keyAlias;
  
  public IamSamlIdpMetadataProperties() {
    // empty constructor
  }

  public String getMetadataUrl() {
    return metadataUrl;
  }

  public void setMetadataUrl(String metadataUrl) {
    this.metadataUrl = metadataUrl;
  }

  public Boolean getRequireValidSignature() {
    return requireValidSignature;
  }

  public void setRequireValidSignature(Boolean requireValidSignature) {
    this.requireValidSignature = requireValidSignature;
  }

  public Boolean getRequireSirtfi() {
    return requireSirtfi;
  }

  public void setRequireSirtfi(Boolean requireSirtfi) {
    this.requireSirtfi = requireSirtfi;
  }

  public Boolean getRequireRs() {
    return requireRs;
  }

  public void setRequireRs(Boolean requireRs) {
    this.requireRs = requireRs;
  }

  public String getKeyAlias() {
    return keyAlias;
  }

  public void setKeyAlias(String keyAlias) {
    this.keyAlias = keyAlias;
  }
}
