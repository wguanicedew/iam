package it.infn.mw.iam.core;

import java.util.HashSet;
import java.util.Set;

import org.mitre.openid.connect.service.ScopeClaimTranslationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

@Service
@Primary
public class IamScopeClaimTranslationService implements ScopeClaimTranslationService {

  private SetMultimap<String, String> scopesToClaims = HashMultimap.create();

  public static final String OPENID_SCOPE = "openid";
  public static final String PROFILE_SCOPE = "profile";
  public static final String EMAIL_SCOPE = "email";
  public static final String PHONE_SCOPE = "phone";
  public static final String ADDRESS_SCOPE = "address";
  
  public IamScopeClaimTranslationService() {
    // Mitreid scope mappings
    scopesToClaims.put(OPENID_SCOPE, "sub");

    scopesToClaims.put(PROFILE_SCOPE, "name");
    scopesToClaims.put(PROFILE_SCOPE, "preferred_username");
    scopesToClaims.put(PROFILE_SCOPE, "given_name");
    scopesToClaims.put(PROFILE_SCOPE, "family_name");
    scopesToClaims.put(PROFILE_SCOPE, "middle_name");
    scopesToClaims.put(PROFILE_SCOPE, "nickname");
    scopesToClaims.put(PROFILE_SCOPE, "profile");
    scopesToClaims.put(PROFILE_SCOPE, "picture");
    scopesToClaims.put(PROFILE_SCOPE, "website");
    scopesToClaims.put(PROFILE_SCOPE, "gender");
    scopesToClaims.put(PROFILE_SCOPE, "zoneinfo");
    scopesToClaims.put(PROFILE_SCOPE, "locale");
    scopesToClaims.put(PROFILE_SCOPE, "updated_at");
    scopesToClaims.put(PROFILE_SCOPE, "birthdate");

    scopesToClaims.put(EMAIL_SCOPE, "email");
    scopesToClaims.put(EMAIL_SCOPE, "email_verified");

    scopesToClaims.put(PHONE_SCOPE, "phone_number");
    scopesToClaims.put(PHONE_SCOPE, "phone_number_verified");

    scopesToClaims.put(ADDRESS_SCOPE, "address");

    // Iam scope mappings
    scopesToClaims.put(PROFILE_SCOPE, "organisation_name");
    scopesToClaims.put(PROFILE_SCOPE, "groups");

  }

  @Override
  public Set<String> getClaimsForScope(String scope) {

    if (scopesToClaims.containsKey(scope)) {
      return scopesToClaims.get(scope);
    } else {
      return new HashSet<>();
    }
  }

  @Override
  public Set<String> getClaimsForScopeSet(Set<String> scopes) {

    Set<String> result = new HashSet<>();
    for (String scope : scopes) {
      result.addAll(getClaimsForScope(scope));
    }
    return result;

  }
}
