package it.infn.mw.iam.config.saml;

import static java.lang.Boolean.FALSE;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

@ConfigurationProperties(prefix = "saml")
public class IamSamlProperties {

  @ConfigurationProperties(prefix = "saml.jit-account-provisioning")
  public static class IamSamlJITAccountProvisioningProperties {

    private Boolean enabled = FALSE;
    private String trustedIdps = "all";
    private Boolean cleanupTaskEnabled = FALSE;

    @Min(5)
    private long cleanupTaskPeriodSec = TimeUnit.DAYS.toSeconds(1);

    @Min(1)
    private Integer inactiveAccountLifetimeDays = 15;

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

    public Boolean getCleanupTaskEnabled() {
      return cleanupTaskEnabled;
    }

    public void setCleanupTaskEnabled(Boolean cleanupEnabled) {
      this.cleanupTaskEnabled = cleanupEnabled;
    }

    public Integer getInactiveAccountLifetimeDays() {
      return inactiveAccountLifetimeDays;
    }

    public void setInactiveAccountLifetimeDays(Integer inactiveUserLifetimeDays) {
      this.inactiveAccountLifetimeDays = inactiveUserLifetimeDays;
    }

    public long getCleanupTaskPeriodSec() {
      return cleanupTaskPeriodSec;
    }

    public void setCleanupTaskPeriodSec(long cleanupTaskPeriodSec) {
      this.cleanupTaskPeriodSec = cleanupTaskPeriodSec;
    }


    public Optional<Set<String>> getTrustedIdpsAsOptionalSet() {
      if ("all".equals(trustedIdps)) {
        return Optional.empty();
      }

      Set<String> trustedIdpIds =
          Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().split(trustedIdps));

      if (trustedIdpIds.isEmpty()) {
        return Optional.empty();
      }

      return Optional.of(trustedIdpIds);
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

  private int metadataLookupServiceRefreshPeriodSec = (int) TimeUnit.MINUTES.toSeconds(5);

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  @NotBlank
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

  public int getMetadataLookupServiceRefreshPeriodSec() {
    return metadataLookupServiceRefreshPeriodSec;
  }

  public void setMetadataLookupServiceRefreshPeriodSec(int metadataLookupServiceRefreshPeriodSec) {
    this.metadataLookupServiceRefreshPeriodSec = metadataLookupServiceRefreshPeriodSec;
  }

}
