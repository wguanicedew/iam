package it.infn.mw.iam.core.oauth.profile.aarc;

import static it.infn.mw.iam.core.oauth.profile.aarc.AarcUserInfoAdapter.forUserInfo;
import static java.util.Objects.isNull;

import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.UserInfoService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.common.BaseUserinfoHelper;

public class AarcJWTProfileUserinfoHelper extends BaseUserinfoHelper {

  protected final AarcUrnHelper aarcUrnHelper;

  public AarcJWTProfileUserinfoHelper(IamProperties props, UserInfoService userInfoService,
      AarcUrnHelper aarcUrnHelper) {
    super(props, userInfoService);
    this.aarcUrnHelper = aarcUrnHelper;
  }

  @Override
  public UserInfo resolveUserInfo(OAuth2Authentication authentication) {

    UserInfo ui = lookupUserinfo(authentication);

    if (isNull(ui)) {
      return null;
    }
    
    return forUserInfo(ui, aarcUrnHelper);
  }

}
