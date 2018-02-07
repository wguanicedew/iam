package it.infn.mw.iam.api.account;

import static java.util.Objects.isNull;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Component
public class AccountUtils {

  IamAccountRepository accountRepo;
  
  @Autowired
  public AccountUtils(IamAccountRepository accountRepo) {
    this.accountRepo = accountRepo;
  }
  
  
  public boolean isAuthenticated() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    return isAuthenticated(auth);
  }
  
  public boolean isAuthenticated(Authentication auth) {
    if (isNull(auth) || auth instanceof AnonymousAuthenticationToken ) {
      return false;
    }
    
    return true;
  }
  
  public Optional<IamAccount> getAuthenticatedUserAccount(Authentication authn){
    if (!isAuthenticated(authn)) {
      return Optional.empty();
    }
    
    if (authn instanceof OAuth2Authentication) {
      OAuth2Authentication oauth = (OAuth2Authentication) authn;
      if (oauth.getUserAuthentication() == null) {
        return Optional.empty();
      }
      authn = oauth.getUserAuthentication();
    }
    
    return accountRepo.findByUsername(authn.getName());
    
  }
  public Optional<IamAccount> getAuthenticatedUserAccount() {
   
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    return getAuthenticatedUserAccount(auth);
      
  }
}
