package it.infn.mw.iam.test.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

public class AuthenticationUtils {

  private AuthenticationUtils() {
    // TODO Auto-generated constructor stub
  }

  public static Authentication adminAuthentication() {
    return new UsernamePasswordAuthenticationToken("admin", "",
        AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN"));
  }
  
  public static Authentication userAuthentication() {
    return new UsernamePasswordAuthenticationToken("test", "",
        AuthorityUtils.createAuthorityList("ROLE_USER"));
  }
}
