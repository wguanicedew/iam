package it.infn.mw.iam.authn.saml;

import static com.google.common.base.Preconditions.checkNotNull;
import static it.infn.mw.iam.authn.saml.util.Saml2Attribute.GIVEN_NAME;
import static it.infn.mw.iam.authn.saml.util.Saml2Attribute.MAIL;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.saml.util.Saml2Attribute;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.core.user.IamAccountService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class JustInTimeProvisioningSAMLUserDetailsService extends SAMLUserDetailsServiceSupport
    implements SAMLUserDetailsService {

  private final IamAccountRepository repo;
  private final IamAccountService accountService;
  private final Optional<Set<String>> trustedIdpEntityIds; 

  private static final EnumSet<Saml2Attribute> REQUIRED_SAML_ATTRIBUTES =
      EnumSet.of(Saml2Attribute.MAIL, Saml2Attribute.GIVEN_NAME, Saml2Attribute.SN);

  public JustInTimeProvisioningSAMLUserDetailsService(SamlUserIdentifierResolver resolver,
      IamAccountService accountService, InactiveAccountAuthenticationHander inactiveAccountHandler,
      IamAccountRepository repo,
      Optional<Set<String>> trustedIdpEntityIds) {
    super(inactiveAccountHandler, resolver);
    this.accountService = accountService;
    this.repo = repo;
    this.trustedIdpEntityIds = trustedIdpEntityIds;
  }

  
  protected void samlCredentialEntityIdChecks(SAMLCredential credential){
    trustedIdpEntityIds.ifPresent(l -> {
      if (!l.contains(credential.getRemoteEntityID())){
        throw new UsernameNotFoundException(
            String.format("Error provisioning user! SAML credential issuer '%s' is not trusted"
                + " for just-in-time account provisioning.", credential.getRemoteEntityID()));
      }
    });
  }
  
  protected void samlCredentialSanityChecks(SAMLCredential credential) {

    for (Saml2Attribute a : REQUIRED_SAML_ATTRIBUTES) {
      if (credential.getAttributeAsString(a.getAttributeName()) == null) {
        throw new UsernameNotFoundException(String.format(
            "Error provisioning user! SAML credential is missing required attribute: %s (%s)",
            a.getAlias(), a.getAttributeName()));
      }
    }
    
    
  }


  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    checkNotNull(credential, "null saml credential");

    IamSamlId samlId = resolverSamlId(credential);

    Optional<IamAccount> account = repo.findBySamlId(samlId);

    if (account.isPresent()) {
      return buildUserFromIamAccount(account.get());
    }

    samlCredentialEntityIdChecks(credential);
    samlCredentialSanityChecks(credential);
    
    final String randomUuid = UUID.randomUUID().toString();

    // Create account from SAMLCredential
    IamAccount newAccount = IamAccount.newAccount();

    newAccount.setProvisioned(true);
    newAccount.getSamlIds().add(samlId);
    samlId.setAccount(newAccount);
    
    newAccount.setActive(true);

    if (samlId.getUserId().length() < 128) {
      newAccount.setUsername(samlId.getUserId());
    } else {
      newAccount.setUsername(randomUuid);
    }

    newAccount.getUserInfo()
      .setGivenName(credential.getAttributeAsString(GIVEN_NAME.getAttributeName()));
    
    newAccount.getUserInfo()
      .setFamilyName(credential.getAttributeAsString(Saml2Attribute.SN.getAttributeName()));
    
    newAccount.getUserInfo().setEmail(credential.getAttributeAsString(MAIL.getAttributeName()));
    
    accountService.createAccount(newAccount);
    
    return buildUserFromIamAccount(newAccount);
  }

}
