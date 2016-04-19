package it.infn.mw.iam.oidc;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OidcProviderTO {

  private String issuer;
  private String cssClass;
  private String descriptor;
  private String clientUri;

  public OidcProviderTO() {
  }

  public String getIssuer() {

    return issuer;
  }

  @JsonProperty("cssclass")
  public String getCssClass() {

    return cssClass;
  }

  public String getDescriptor() {

    return descriptor;
  }

  @JsonProperty("client_uri")
  public String getClientUri() {

    return clientUri;
  }

  public void setIssuer(String issuer) {

    this.issuer = issuer;
  }

  public void setCssClass(String cssClass) {

    this.cssClass = cssClass;
  }

  public void setDescriptor(String descriptor) {

    this.descriptor = descriptor;
  }

  public void setClientUri(String clientUri) {

    this.clientUri = clientUri;
  }

}
