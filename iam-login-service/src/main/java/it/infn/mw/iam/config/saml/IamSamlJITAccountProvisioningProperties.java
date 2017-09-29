package it.infn.mw.iam.config.saml;

import static java.lang.Boolean.FALSE;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

@ConfigurationProperties(prefix = "saml.jit-account-provisioning")
public class IamSamlJITAccountProvisioningProperties {

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