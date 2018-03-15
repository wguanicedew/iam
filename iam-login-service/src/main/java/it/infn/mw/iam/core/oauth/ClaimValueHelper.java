package it.infn.mw.iam.core.oauth;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableSet;

import it.infn.mw.iam.core.IamProperties;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.model.IamUserInfo;

@Component
public class ClaimValueHelper {

  public static final Set<String> ADDITIONAL_CLAIMS =
      ImmutableSet.of("name", "email", "preferred_username", "organisation_name", "groups");

  private String organisationName = IamProperties.INSTANCE.getOrganisationName();

  public Object getClaimValueFromUserInfo(String claim, IamUserInfo info) {

    switch (claim) {

      case "name":
        return info.getName();

      case "email":
        return info.getEmail();

      case "preferred_username":
        return info.getPreferredUsername();

      case "organisation_name":
        return organisationName;

      case "groups":
        return info.getGroups().stream().map(IamGroup::getName).toArray(String[]::new);

      default:
        return null;
    }
  }

}
