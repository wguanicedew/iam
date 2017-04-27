package it.infn.mw.iam.authn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.saml.SAMLCredential;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.authn.oidc.OidcExternalAuthenticationToken;
import it.infn.mw.iam.authn.saml.SamlExternalAuthenticationToken;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;

@Component
public class DefaultExternalAuthenticationInfoBuilder implements ExternalAuthenticationInfoBuilder {

  public static final String TYPE_ATTR = "type";
  public static final String OIDC_TYPE = "oidc";
  public static final String SAML_TYPE = "saml";

  public DefaultExternalAuthenticationInfoBuilder() {}

  public Map<String, String> buildInfoMap(OidcExternalAuthenticationToken token) {
    checkNotNull(token, "token cannot be null");

    Map<String, String> result = new HashMap<>();

    result.put(TYPE_ATTR, OIDC_TYPE);
    result.put("sub", token.getExternalAuthentication().getSub());
    result.put("iss", token.getExternalAuthentication().getIssuer());

    return result;
  }

  public Map<String, String> buildInfoMap(SamlExternalAuthenticationToken token) {
    checkNotNull(token, "token cannot be null");
    Map<String, String> result = new HashMap<>();

    result.put(TYPE_ATTR, SAML_TYPE);

    SAMLCredential cred = (SAMLCredential) token.getExternalAuthentication().getCredentials();
    
    result.put("idpEntityId", cred.getRemoteEntityID());

    for (Saml2Attribute attr: Saml2Attribute.values()){
      String attrVal = cred.getAttributeAsString(attr.getAttributeName()); 
      if ( attrVal != null) {
        result.put(attr.name(), attrVal);
      }
    }

    return result;
  }

}
