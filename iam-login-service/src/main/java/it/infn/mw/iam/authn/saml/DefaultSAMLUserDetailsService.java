package it.infn.mw.iam.authn.saml;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport;
import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.authn.util.AuthenticationUtils;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class DefaultSAMLUserDetailsService implements SAMLUserDetailsService {

  final SamlUserIdentifierResolver resolver;
  final IamAccountRepository repo;

  final InactiveAccountAuthenticationHander inactiveAccountHandler;

  @Autowired
  public DefaultSAMLUserDetailsService(SamlUserIdentifierResolver resolver,
      IamAccountRepository repo, InactiveAccountAuthenticationHander handler) {
    this.resolver = resolver;
    this.repo = repo;
    this.inactiveAccountHandler = handler;
  }
  
  protected User buildUserFromIamAccount(IamAccount account) {
    inactiveAccountHandler.handleInactiveAccount(account);
    return AuthenticationUtils.userFromIamAccount(account);
  }

  protected User buildUserFromSamlCredential(IamSamlId samlId, SAMLCredential credential) {
    return new User(samlId.getUserId(), "",
        Arrays.asList(ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH));
  }

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
    
    IamSamlId userSamlId =
        resolver.getSamlUserIdentifier(credential).orElseThrow(() -> new UsernameNotFoundException(
            "Could not extract a user identifier from the SAML assertion"));

    Optional<IamAccount> account = repo.findBySamlId(userSamlId);

    if (account.isPresent()) {
      return buildUserFromIamAccount(account.get());
    }

    return buildUserFromSamlCredential(userSamlId, credential);
  }

}
