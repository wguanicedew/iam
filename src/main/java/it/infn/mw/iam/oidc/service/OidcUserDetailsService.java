package it.infn.mw.iam.oidc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.mitre.openid.connect.client.SubjectIssuerGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import it.infn.mw.iam.persistence.model.Authority;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRespository;

public class OidcUserDetailsService {

  @Autowired
  IamAccountRespository repo;

  List<GrantedAuthority> convertAuthorities(IamAccount a) {

    List<GrantedAuthority> authorities = new ArrayList<>();
    for (Authority auth : a.getAuthorities()) {
      String data = auth.getAuthority();
      GrantedAuthority elem = null;
      if (data.startsWith("OIDC_")) {
        String[] parts = data.split("_");
        elem = new SubjectIssuerGrantedAuthority(parts[1], parts[2]);
      } else {
        elem = new SimpleGrantedAuthority(data);
      }
      authorities.add(elem);
    }
    return authorities;
  }

  public Object loadUserByOIDC(String subject, String issuer) {

    Optional<IamAccount> account = repo.findByOidcAccount(issuer, subject);

    if (account.isPresent()) {

      IamAccount a = account.get();

      User u = new User(a.getUsername(), a.getPassword(),
        convertAuthorities(a));

      return u;

    }

    return null;

  }

}
