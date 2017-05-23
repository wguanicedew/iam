package it.infn.mw.iam.config.saml;

import static java.lang.Boolean.FALSE;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "saml")
public class IamSamlProperties {

  @ConfigurationProperties(prefix = "saml.jit-user-provisioning")
  public static class IamSamlJITUserProvisioningProperties {

    private Boolean enabled = FALSE;
    private String trustedIdps = "all";

    public IamSamlJITUserProvisioningProperties() {}

    public Boolean getEnabled() {
      return enabled;
    }

    public void setEnabled(Boolean enabled) {
      this.enabled = enabled;
    }

    public String getTrustedIdps() {
      return trustedIdps;
    }

    public void setTrustedIdps(String trustedIdps) {
      this.trustedIdps = trustedIdps;
    }
  }

  private String entityId;
  private String idpMetadata;
  private String keystore;
  private String keystorePassword;
  private String keyId;
  private String keyPassword;
  private String idResolvers;

  private int maxAssertionTimeSec;

  private int maxAuthenticationAgeSec;

  public IamSamlProperties() {}

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getIdpMetadata() {
    return idpMetadata;
  }

  public void setIdpMetadata(String idpMetadata) {
    this.idpMetadata = idpMetadata;
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
  
}
