package it.infn.mw.iam.core.oauth.profile.aarc;

import static it.infn.mw.iam.core.oauth.profile.common.BaseAccessTokenBuilder.AARC_GROUPS_CLAIM_NAME;

import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.nimbusds.jwt.JWTClaimsSet.Builder;

import it.infn.mw.iam.api.account.password_reset.error.UserNotFoundError;
import it.infn.mw.iam.core.oauth.profile.common.BaseIdTokenCustomizer;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class AarcJWTProfileIdTokenCustomizer extends BaseIdTokenCustomizer {

  protected final AarcUrnHelper aarcUrnHelper;

  public AarcJWTProfileIdTokenCustomizer(IamAccountRepository accountRepo,
      AarcUrnHelper aarcUrnHelper) {
    super(accountRepo);
    this.aarcUrnHelper = aarcUrnHelper;
  }

  @Override
  public void customizeIdTokenClaims(Builder idClaims, ClientDetailsEntity client,
      OAuth2Request request, String sub, OAuth2AccessTokenEntity accessToken) {

    IamAccount account = getAccountRepo().findByUuid(sub)
      .orElseThrow(() -> new UserNotFoundError(String.format("No user found for uuid %s", sub)));
    IamUserInfo info = account.getUserInfo();

    idClaims.claim(AARC_GROUPS_CLAIM_NAME, aarcUrnHelper.resolveGroups(info.getGroups()));
  }

}
