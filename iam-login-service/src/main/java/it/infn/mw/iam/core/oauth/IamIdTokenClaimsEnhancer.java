package it.infn.mw.iam.core.oauth;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mitre.oauth2.model.OAuth2AccessTokenEntity;
import org.mitre.openid.connect.service.IDTokenClaimsEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Request;
import com.nimbusds.jwt.JWTClaimsSet;
import it.infn.mw.iam.api.account.password_reset.error.UserNotFoundError;
import it.infn.mw.iam.core.IamProperties;
import it.infn.mw.iam.core.IamScopeClaimTranslationService;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamUserInfo;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamUserinfoRepository;

public class IamIdTokenClaimsEnhancer implements IDTokenClaimsEnhancer {

  @Autowired
  IamUserinfoRepository iamUserInfoRepository;

  @Autowired
  IamAccountRepository iamAccountRepository;

  @Autowired
  IamScopeClaimTranslationService scopeClaimConverter;

  private String organisationName = IamProperties.INSTANCE.getOrganisationName();

  private final Set<String> enhancedClaims = new HashSet<String>() {

    private static final long serialVersionUID = 1L;

    {
      add("email");
      add("preferred_username");
      add("organisation_name");
      add("groups");
    }
  };

  @Override
  public void enhanceIdTokenClaims(JWTClaimsSet.Builder claimsBuilder, OAuth2Request request, Date issueTime,
      String sub, OAuth2AccessTokenEntity accessToken) {

    IamAccount account = iamAccountRepository.findByUuid(sub)
        .orElseThrow(() -> new UserNotFoundError("No user found for uuid " + sub));
    IamUserInfo info = account.getUserInfo();

    Set<String> requiredClaims = scopeClaimConverter.getClaimsForScopeSet(request.getScope());

    for (String claim: requiredClaims) {
      if (enhancedClaims.contains(claim)) {
        claimsBuilder.claim(claim, getClaimValueFromUserInfo(claim, info));
      }
    }
  }

  private Object getClaimValueFromUserInfo(String claim, IamUserInfo info) {

    if ("email".equals(claim)) {
      return info.getEmail();
    }
    if ("preferred_username".equals(claim)) {
      return info.getPreferredUsername();
    }
    if ("organisation_name".equals(claim)) {
      return organisationName;
    }
    if ("groups".equals(claim)) {
      List<String> names =
          info.getGroups().stream().map(IamGroup::getName).collect(Collectors.toList());
      return names.toArray(new String[0]);
    }

    return null;
  }
}
