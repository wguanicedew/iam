package it.infn.mw.iam.api.scim.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.util.ssh.InvalidSshKeyException;
import it.infn.mw.iam.util.ssh.RSAPublicKey;

@JsonInclude(Include.NON_EMPTY)
public class ScimSshKey {

  @Length(max = 36)
  private final String display;

  private final Boolean primary;

  @Length(max = 48)
  private String fingerprint;

  @NotBlank
  private final String value;

  @JsonIgnore
  private ScimMemberRef accountRef;

  @JsonIgnore
  private RSAPublicKey key;

  @JsonCreator
  private ScimSshKey(@JsonProperty("display") String display,
      @JsonProperty("primary") Boolean primary, @JsonProperty("value") String value,
      @JsonProperty("fingerprint") String fingerprint) {

    this.display = display;
    this.value = value;
    if (value == null) {
      throw new IllegalArgumentException("ScimSshKey value cannot be null");
    }
    this.primary = primary;
    try {
      this.key = new RSAPublicKey(value);
    } catch (InvalidSshKeyException e) {
      throw new ScimException(e.getMessage());
    }
    if (fingerprint != null) {
      if (!fingerprint.equals(key.getSHA256Fingerprint())) {
        throw new ScimException(
            "Cannot assign fingerprint " + fingerprint + " to the ssh key " + key);
      }
      this.fingerprint = fingerprint;
    } else {
      this.fingerprint = key.getSHA256Fingerprint();
    }
  }

  public String getDisplay() {

    return display;
  }

  public String getFingerprint() {

    return fingerprint;
  }

  public String getValue() {

    return value;
  }

  public Boolean isPrimary() {

    return primary;
  }

  @JsonIgnore
  public ScimMemberRef getAccountRef() {

    return accountRef;
  }

  private ScimSshKey(Builder b) {

    this.display = b.display;
    this.primary = b.primary;
    this.value = b.value;
    try {
      this.key = new RSAPublicKey(value);
    } catch (InvalidSshKeyException e) {
      throw new ScimException(e.getMessage());
    }
    if (b.fingerprint != null) {
      if (!b.fingerprint.equals(this.key.getSHA256Fingerprint())) {
        throw new ScimException(
            "Cannot assign fingerprint " + fingerprint + " to the ssh key " + value);
      }
      this.fingerprint = b.fingerprint;
    } else {
      this.fingerprint = key.getSHA256Fingerprint();
    }
    this.accountRef = b.accountRef;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private String display;
    private String value;
    private Boolean primary;
    private String fingerprint;
    private ScimMemberRef accountRef;

    public Builder() {

    }

    public Builder display(String display) {

      this.display = display;
      return this;
    }

    public Builder value(String value) {

      this.value = value;
      return this;
    }

    public Builder fingerprint(String fingerprint) {

      this.fingerprint = fingerprint;
      return this;
    }

    public Builder primary(Boolean primary) {

      this.primary = primary;
      return this;
    }

    public Builder accountRef(ScimMemberRef accountRef) {

      this.accountRef = accountRef;
      return this;
    }

    public ScimSshKey build() {

      return new ScimSshKey(this);
    }
  }
}
