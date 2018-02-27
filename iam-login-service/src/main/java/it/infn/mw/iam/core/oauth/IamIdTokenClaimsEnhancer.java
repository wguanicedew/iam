package it.infn.mw.iam.core.oauth;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.service.IDTokenClaimsEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Request;
import com.google.common.collect.Sets;
import com.nimbusds.jwt.JWTClaimsSet;
import it.infn.mw.iam.api.account.password_reset.error.UserNotFoundError;
import it.infn.mw.iam.core.IamProperties;
import it.infn.mw.iam.core.IamScopeClaimTranslationService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class IamIdTokenClaimsEnhancer implements IDTokenClaimsEnhancer {

  @Autowired
  private IamAccountRepository iamAccountRepository;

  @Autowired
  private IamScopeClaimTranslationService scopeClaimConverter;

  private String organisationName = IamProperties.INSTANCE.getOrganisationName();

  private static final Set<String> enhancedClaims =
      Sets.newHashSet("email", "preferred_username", "organisation_name", "groups");

  @Override
  public void enhanceIdTokenClaims(JWTClaimsSet.Builder claimsBuilder, OAuth2Request request,
      Date issueTime, String sub, OAuth2AccessTokenEntity accessToken) {

    IamAccount account = iamAccountRepository.findByUuid(sub)
        .orElseThrow(() -> new UserNotFoundError(String.format("No user found for uuid %s", sub)));
    IamUserInfo info = account.getUserInfo();

    Set<String> requiredClaims = scopeClaimConverter.getClaimsForScopeSet(request.getScope());

    requiredClaims.stream().filter(IamIdTokenClaimsEnhancer::isEnhancedClaim)
        .forEach(c -> claimsBuilder.claim(c, getClaimValueFromUserInfo(c, info)));
  }

  private static boolean isEnhancedClaim(String claim) {

    return enhancedClaims.contains(claim);
  }

  private Object getClaimValueFromUserInfo(String claim, IamUserInfo info) {

    switch (claim) {
      case "email":
        return info.getEmail();
      case "preferred_username":
        return info.getPreferredUsername();
      case "organisation_name":
        return organisationName;
      case "groups":
        List<String> names =
            info.getGroups().stream().map(IamGroup::getName).collect(Collectors.toList());
        return names.toArray(new String[0]);
      default:
        return null;
    }
  }
}
