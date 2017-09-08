package it.infn.mw.iam.test.repository;

import java.util.Date;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;

public class ScopePolicyTestUtils {
  
  public static final String SCIM_READ = "scim:read";
  public static final String SCIM_WRITE = "scim:write";
  public static final String OPENID = "openid";
  public static final String PROFILE = "profile";
  public static final String WHATEVER = "whatever";
  
  private IamScopePolicy initScopePolicy() {
    Date now = new Date();
    IamScopePolicy p = new IamScopePolicy();
    p.setCreationTime(now);
    p.setLastUpdateTime(now);
    return p;
  }
  protected IamScopePolicy initDenyScopePolicy(){
    IamScopePolicy p = initScopePolicy();
    p.setRule(Rule.DENY);
    return p;
  }
  
  protected IamScopePolicy initPermitScopePolicy(){
    IamScopePolicy p = initScopePolicy();
    p.setRule(Rule.PERMIT);
    return p;
  }
  
  public Authentication anonymousAuthenticationToken() {
    return new AnonymousAuthenticationToken("key", "anonymous",
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
  }

}
