package it.infn.mw.iam.authn.saml;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

import it.infn.mw.iam.authn.InactiveAccountAuthenticationHander;
import it.infn.mw.iam.authn.saml.util.SamlUserIdentifierResolver;
import it.infn.mw.iam.authn.util.AuthenticationUtils;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class DefaultSAMLUserDetailsService extends SAMLUserDetailsServiceSupport
    implements SAMLUserDetailsService {

  final IamAccountRepository repo;

  @Autowired
  public DefaultSAMLUserDetailsService(SamlUserIdentifierResolver resolver,
      IamAccountRepository repo, InactiveAccountAuthenticationHander handler) {
    super(handler, resolver);
    this.repo = repo;
  }

  @Override
  public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {

    IamSamlId samlId = resolverSamlId(credential);

    Optional<IamAccount> account = repo.findBySamlId(samlId);

    if (account.isPresent()) {
      return buildUserFromIamAccount(account.get());
    }

    return buildUserFromSamlCredential(samlId, credential);
  }

}
