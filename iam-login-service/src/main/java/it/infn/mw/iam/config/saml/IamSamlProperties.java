package it.infn.mw.iam.config.saml;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saml")
public class IamSamlProperties {

  private String entityId;
  
  private String keystore;
  
  private String keystorePassword;
  
  private String keyId;
  
  private String keyPassword ;
  
  private String idResolvers;
  
  private String loginButtonText;

  private int maxAssertionTimeSec;

  private int maxAuthenticationAgeSec;

  private int metadataLookupServiceRefreshPeriodSec = (int) TimeUnit.MINUTES.toSeconds(5);

  private String idpEntityIdWhilelist;
  
  private List<IamSamlIdpMetadataProperties> idpMetadata;
  
  public List<IamSamlIdpMetadataProperties> getIdpMetadata() {
    return idpMetadata;
  }

  public void setIdpMetadata(List<IamSamlIdpMetadataProperties> idpMetadata) {
    this.idpMetadata = idpMetadata;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getKeystore() {
    return keystore;
  }

  public void setKeystore(String keystore) {
    this.keystore = keystore;
  }

  public String getKeystorePassword() {
    return keystorePassword;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getKeyPassword() {
    return keyPassword;
  }

  public void setKeyPassword(String keyPassword) {
    this.keyPassword = keyPassword;
  }

  public int getMaxAssertionTimeSec() {
    return maxAssertionTimeSec;
  }

  public void setMaxAssertionTimeSec(int maxAssertionTimeSec) {
    this.maxAssertionTimeSec = maxAssertionTimeSec;
  }

  public int getMaxAuthenticationAgeSec() {
    return maxAuthenticationAgeSec;
  }

  public void setMaxAuthenticationAgeSec(int maxAuthenticationAgeSec) {
    this.maxAuthenticationAgeSec = maxAuthenticationAgeSec;
  }

  public String getIdResolvers() {
    return idResolvers;
  }

  public void setIdResolvers(String idResolvers) {
    this.idResolvers = idResolvers;
  }

  public int getMetadataLookupServiceRefreshPeriodSec() {
    return metadataLookupServiceRefreshPeriodSec;
  }

  public void setMetadataLookupServiceRefreshPeriodSec(int metadataLookupServiceRefreshPeriodSec) {
    this.metadataLookupServiceRefreshPeriodSec = metadataLookupServiceRefreshPeriodSec;
  }

  public String getLoginButtonText() {
    return loginButtonText;
  }

  public void setLoginButtonText(String loginButtonText) {
    this.loginButtonText = loginButtonText;
  }

  public String getIdpEntityIdWhilelist() {
    return idpEntityIdWhilelist;
  }

  public void setIdpEntityIdWhilelist(String idpEntityIdWhilelist) {
    this.idpEntityIdWhilelist = idpEntityIdWhilelist;
  }
}
