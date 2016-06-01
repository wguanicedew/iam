package it.infn.mw.iam.oidc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAuthority;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class OidcUserDetailsService {

  @Autowired
  IamAccountRepository repo;

  List<GrantedAuthority> convertAuthorities(IamAccount a) {

    List<GrantedAuthority> authorities = new ArrayList<>();

    for (IamAuthority auth : a.getAuthorities()) {

      authorities.add(new SimpleGrantedAuthority(auth.getAuthority()));

    }
    return authorities;
  }

  public Object loadUserByOIDC(String subject, String issuer) {

    Optional<IamAccount> account = repo.findByOidcId(issuer, subject);

    if (account.isPresent()) {

      IamAccount a = account.get();

      User u = new User(a.getUsername(), a.getPassword(),
        convertAuthorities(a));

      return u;

    } else {

      String oidcSubject = String.format("\"%s:%s\"", subject, issuer);

      throw new UsernameNotFoundException(
        "No user found linked with OpenID connect subject " + oidcSubject);
    }

  }

}
