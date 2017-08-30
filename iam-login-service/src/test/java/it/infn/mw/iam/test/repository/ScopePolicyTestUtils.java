package it.infn.mw.iam.test.repository;

import java.util.Date;

import it.infn.mw.iam.persistence.model.IamScope;
import it.infn.mw.iam.persistence.model.IamScopePolicy;
import it.infn.mw.iam.persistence.model.IamScopePolicy.Rule;

public class ScopePolicyTestUtils {
  
  public static final String SCIM_READ = "scim:read";
  public static final String SCIM_WRITE = "scim:write";
  public static final String OPENID = "openid";
  public static final String PROFILE = "profile";
  
  public static final IamScope SCIM_READ_SCOPE = new IamScope(SCIM_READ); 
  public static final IamScope SCIM_WRITE_SCOPE = new IamScope(SCIM_WRITE);
  public static final IamScope OPENID_SCOPE = new IamScope(OPENID);
  public static final IamScope PROFILE_SCOPE = new IamScope(PROFILE);
  public static final IamScope WHATEVER_SCOPE = new IamScope("whatever");

  
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

}
