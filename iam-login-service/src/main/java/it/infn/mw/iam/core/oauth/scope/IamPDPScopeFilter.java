package it.infn.mw.iam.core.oauth.scope;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
@ConditionalOnProperty(name = "iam.enableScopeAuthz", havingValue = "true")
public class IamPDPScopeFilter implements IamScopeFilter {

  final ScopePolicyPDP pdp;
  final IamAccountRepository accountRepo;

  @Autowired
  public IamPDPScopeFilter(ScopePolicyPDP pdp, IamAccountRepository accountRepo) {
    this.pdp = pdp;
    this.accountRepo = accountRepo;
  }

  protected Optional<IamAccount> resolveIamAccount(Authentication authn) {
    
    if (authn == null){
      return Optional.empty();
    }
    
    Authentication userAuthn = authn;
    
    if (authn instanceof OAuth2Authentication){
      userAuthn = ((OAuth2Authentication) authn).getUserAuthentication();
    }
    
    if (userAuthn == null) {
      return Optional.empty();
    }

    String principalName = authn.getName();
    return accountRepo.findByUsername(principalName);
  }

  @Override
  public void filterScopes(Set<String> scopes, Authentication authn) {
    
    Optional<IamAccount> maybeAccount = resolveIamAccount(authn);

    if (maybeAccount.isPresent()) {
      Set<String> filteredScopes =
          pdp.filterScopes(scopes, maybeAccount.get());
      
      scopes.retainAll(filteredScopes);
    }

  }

}
