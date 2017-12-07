package it.infn.mw.iam.api.account;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
  
  public Optional<IamAccount> getAuthenticatedUserAccount() {
    
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth instanceof OAuth2Authentication) {
      OAuth2Authentication oauth = (OAuth2Authentication) auth;
      if (oauth.getUserAuthentication() == null) {
        return Optional.empty();
      }
      auth = oauth.getUserAuthentication();
    }
    
    return accountRepo.findByUsername(auth.getName()); 
  }
}
