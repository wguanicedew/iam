package it.infn.mw.iam.api.scim.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ScimAddress {

  public static enum ScimAddressType {
    work, home, other;
  }

  private final ScimAddressType type;

  private final String formatted;
  private final String streetAddress;
  private final String locality;
  private final String region;
  private final String postalCode;
  private final String country;

  private final Boolean primary;

  @JsonCreator
  private ScimAddress(@JsonProperty("type") ScimAddressType type,
      @JsonProperty("formatted") String formatted,
      @JsonProperty("streetAddress") String streetAddress,
      @JsonProperty("locality") String locality, @JsonProperty("region") String region,
      @JsonProperty("postalCode") String postalCode, @JsonProperty("country") String country,
      @JsonProperty("primary") Boolean primary) {

    this.type = type;
    this.formatted = formatted;
    this.streetAddress = streetAddress;
    this.locality = locality;
    this.region = region;
    this.postalCode = postalCode;
    this.country = country;
    this.primary = primary;
  }

  private ScimAddress(Builder b) {
    this.formatted = b.formatted;
    this.streetAddress = b.streetAddress;
    this.locality = b.locality;
    this.region = b.region;
    this.postalCode = b.postalCode;
    this.country = b.country;
    this.primary = b.primary;
    this.type = b.type;
  }

  public ScimAddressType getType() {

    return type;
  }

  public String getFormatted() {

    return formatted;
  }

  public String getStreetAddress() {

    return streetAddress;
  }

  public String getLocality() {

    return locality;
  }

  public String getRegion() {

    return region;
  }

  public String getPostalCode() {

    return postalCode;
  }

  public String getCountry() {

    return country;
  }

  public Boolean getPrimary() {

    return primary;
  }

  public static Builder builder() {

    return new Builder();
  }

  public static class Builder {

    private ScimAddressType type = ScimAddressType.work;

    private String formatted;
    private String streetAddress;
    private String locality;
    private String region;
    private String postalCode;
    private String country;
    private boolean primary = true;

    public Builder() {

    }

    public Builder formatted(String formatted) {

      this.formatted = formatted;
      return this;
    }

    public Builder streetAddress(String streetAddress) {

      this.streetAddress = streetAddress;
      return this;
    }

    public Builder locality(String locality) {

      this.locality = locality;
      return this;
    }

    public Builder region(String region) {

      this.region = region;
      return this;
    }

    public Builder postalCode(String postalCode) {

      this.postalCode = postalCode;
      return this;
    }

    public Builder country(String country) {

      this.country = country;
      return this;
    }

    public ScimAddress build() {

      return new ScimAddress(this);
    }
  }

}
