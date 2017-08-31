package it.infn.mw.iam.authn.util;

import java.util.List;
import java.util.stream.Collectors;

import org.mitre.oauth2.model.SavedUserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.google.common.collect.ImmutableSet;

import it.infn.mw.iam.authn.oidc.OidcExternalAuthenticationToken;
import it.infn.mw.iam.authn.saml.SamlExternalAuthenticationToken;
import it.infn.mw.iam.persistence.model.IamAccount;

public class AuthenticationUtils {

  private static final ImmutableSet<String> SUPPORTED_EXTERNAL_AUTHN_TOKENS =
      ImmutableSet.of(SamlExternalAuthenticationToken.class.getName(),
          OidcExternalAuthenticationToken.class.getName());

  private AuthenticationUtils() {}
  
  public static boolean isSupportedExternalAuthenticationToken(Authentication authn) {

    if (authn instanceof SavedUserAuthentication) {
      SavedUserAuthentication savedAuth = (SavedUserAuthentication) authn;

      if (savedAuth.getSourceClass() != null) {
        return SUPPORTED_EXTERNAL_AUTHN_TOKENS.contains(savedAuth.getSourceClass());
      }

    }
    return false;
  }

  public static List<GrantedAuthority> convertIamAccountAuthorities(IamAccount account) {
    return account.getAuthorities()
      .stream()
      .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
      .collect(Collectors.toList());
  }
  
  public static User userFromIamAccount(IamAccount account){
    return new User(account.getUsername(), account.getPassword(), convertIamAccountAuthorities(account));
  }

  

}
