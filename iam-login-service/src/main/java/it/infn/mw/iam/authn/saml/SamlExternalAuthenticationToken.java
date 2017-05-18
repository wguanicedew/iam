package it.infn.mw.iam.authn.saml;

import static it.infn.mw.iam.authn.saml.util.Saml2Attribute.givenName;
import static it.infn.mw.iam.authn.saml.util.Saml2Attribute.mail;
import static it.infn.mw.iam.authn.saml.util.Saml2Attribute.sn;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.security.saml.SAMLCredential;

import com.google.common.base.Strings;

import it.infn.mw.iam.authn.AbstractExternalAuthenticationToken;
import it.infn.mw.iam.authn.ExternalAccountLinker;
import it.infn.mw.iam.authn.ExternalAuthenticationInfoBuilder;
import it.infn.mw.iam.authn.ExternalAuthenticationRegistrationInfo;
import it.infn.mw.iam.authn.ExternalAuthenticationRegistrationInfo.ExternalAuthenticationType;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

public class SamlExternalAuthenticationToken
    extends AbstractExternalAuthenticationToken<ExpiringUsernameAuthenticationToken> {

  private static final long serialVersionUID = -7854473523011856692L;
  
  private final IamSamlId samlId;
  
  public SamlExternalAuthenticationToken(IamSamlId samlId, ExpiringUsernameAuthenticationToken authn,
      Date tokenExpiration, Object principal, Object credentials,
      Collection<? extends GrantedAuthority> authorities) {
    super(authn, tokenExpiration, principal, credentials, authorities);
    this.samlId = samlId;
  }

  @Override
  public Map<String, String> buildAuthnInfoMap(ExternalAuthenticationInfoBuilder visitor) {

    return visitor.buildInfoMap(this);
  }

  @Override
  public ExternalAuthenticationRegistrationInfo toExernalAuthenticationRegistrationInfo() {

    ExternalAuthenticationRegistrationInfo ri =
	new ExternalAuthenticationRegistrationInfo(ExternalAuthenticationType.SAML);

    SAMLCredential cred = (SAMLCredential) getExternalAuthentication().getCredentials();

    ri.setIssuer(samlId.getIdpId());
    ri.setSubject(samlId.getUserId());
    ri.setSubjectAttribute(samlId.getAttributeId());

    if (!Strings.isNullOrEmpty(cred.getAttributeAsString(givenName.getAttributeName()))) {
      ri.setGivenName(cred.getAttributeAsString(givenName.getAttributeName()));
    }

    if (!Strings.isNullOrEmpty(cred.getAttributeAsString(sn.getAttributeName()))) {
      ri.setFamilyName(cred.getAttributeAsString(sn.getAttributeName()));
    }

    if (!Strings.isNullOrEmpty(cred.getAttributeAsString(mail.getAttributeName()))) {
      ri.setEmail(cred.getAttributeAsString(mail.getAttributeName()));
    }

    return ri;
  }

  @Override
  public void linkToIamAccount(ExternalAccountLinker visitor, IamAccount account) {
    visitor.linkToIamAccount(account, this);
  }

  public IamSamlId getSamlId() {
    return samlId;
  }
}
