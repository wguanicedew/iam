package it.infn.mw.iam.authn.oidc.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class DefaultOidcUserDetailsService implements OidcUserDetailsService {

  @Autowired
  IamAccountRepository repo;
  
  List<GrantedAuthority> convertAuthorities(IamAccount a) {

    List<GrantedAuthority> authorities = new ArrayList<>();

    for (IamAuthority auth : a.getAuthorities()) {

      authorities.add(new SimpleGrantedAuthority(auth.getAuthority()));

    }
    return authorities;
  }

  @Override
  public Object loadUserByOIDC(String subject, String issuer) {

    IamAccount account =
        repo.findByOidcId(issuer, subject).orElseThrow(() -> new UsernameNotFoundException(String
          .format("No user found linked with OpenID connect subject \"%s:%s\"", issuer, subject)));

    return new User(account.getUsername(), account.getPassword(), convertAuthorities(account));
    
  }

}
