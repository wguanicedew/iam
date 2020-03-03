package it.infn.mw.iam.core.oauth.profile.aarc;

import static it.infn.mw.iam.core.oauth.profile.common.BaseAccessTokenBuilder.AARC_GROUPS_CLAIM_NAME;

import java.util.Map;
import java.util.Set;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.oauth2.service.IntrospectionResultAssembler;
import org.mitre.openid.connect.model.UserInfo;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.common.BaseIntrospectionHelper;
import it.infn.mw.iam.core.oauth.scope.matchers.ScopeMatcherRegistry;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.UserInfoAdapter;

public class AarcJWTProfileTokenIntrospectionHelper extends BaseIntrospectionHelper {

  protected final AarcUrnHelper aarcUrnHelper;

  public AarcJWTProfileTokenIntrospectionHelper(IamProperties props,
      IntrospectionResultAssembler assembler, ScopeMatcherRegistry scopeMatchersRegistry,
      AarcUrnHelper aarcUrnHelper) {
    super(props, assembler, scopeMatchersRegistry);
    this.aarcUrnHelper = aarcUrnHelper;
  }

  @Override
  public Map<String, Object> assembleIntrospectionResult(OAuth2AccessTokenEntity accessToken,
      UserInfo userInfo, Set<String> authScopes) {

    Map<String, Object> result = getAssembler().assembleFrom(accessToken, userInfo, authScopes);

    addIssuerClaim(result);
    addAudience(result, accessToken);

    Set<String> scopes = filterScopes(accessToken, authScopes);
    
    addScopeClaim(result, scopes);

    if (userInfo != null) {

      IamUserInfo iamUserInfo = ((UserInfoAdapter) userInfo).getUserinfo();

      if (!iamUserInfo.getGroups().isEmpty()) {

        result.put(AARC_GROUPS_CLAIM_NAME, aarcUrnHelper.resolveGroups(iamUserInfo.getGroups()));
      }
    }

    return result;
  }

}
