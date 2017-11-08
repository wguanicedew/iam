package it.infn.mw.iam.test.util;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithAnonymousUserSecurityContextFactory
    implements WithSecurityContextFactory<WithAnonymousUser> {

  @Override
  public SecurityContext createSecurityContext(WithAnonymousUser annotation) {
    List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS");
    Authentication authentication =
        new AnonymousAuthenticationToken("key", "anonymous", authorities);
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }

}
