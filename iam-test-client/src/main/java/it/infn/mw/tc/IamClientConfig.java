package it.infn.mw.tc;

import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.oauth2.model.RegisteredClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iam")
public class IamClientConfig extends RegisteredClient {

  String issuer;

  String extAuthnHint;

  public IamClientConfig() {
    setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
  }

  public String getIssuer() {

    return issuer;
  }

  public void setIssuer(String issuer) {

    this.issuer = issuer;
  }

  public String getExtAuthnHint() {
    return extAuthnHint;
  }

  public void setExtAuthnHint(String extAuthnHint) {
    this.extAuthnHint = extAuthnHint;
  }

}
