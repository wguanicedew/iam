package it.infn.mw.iam.core.oauth.profile.aarc;

import static java.util.Objects.isNull;

import java.time.Instant;
import java.util.Set;

import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.config.IamProperties;
import it.infn.mw.iam.core.oauth.profile.common.BaseAccessTokenBuilder;
import it.infn.mw.iam.persistence.repository.UserInfoAdapter;

public class AarcJWTProfileAccessTokenBuilder extends BaseAccessTokenBuilder {

  protected final AarcUrnHelper aarcUrnHelper;

  public AarcJWTProfileAccessTokenBuilder(IamProperties properties, AarcUrnHelper aarcUrnHelper) {
    super(properties);
    this.aarcUrnHelper = aarcUrnHelper;
  }

  @Override
  public JWTClaimsSet buildAccessToken(OAuth2AccessTokenEntity token,
      OAuth2Authentication authentication, UserInfo userInfo, Instant issueTime) {

    Builder builder = baseJWTSetup(token, authentication, userInfo, issueTime);

    if (!isNull(userInfo)) {
      Set<String> groupUrns =
          aarcUrnHelper.resolveGroups(((UserInfoAdapter) userInfo).getUserinfo().getGroups());

      if (!groupUrns.isEmpty()) {
        builder.claim(AARC_GROUPS_CLAIM_NAME, groupUrns);
      }
    }

    return builder.build();
  }

}
