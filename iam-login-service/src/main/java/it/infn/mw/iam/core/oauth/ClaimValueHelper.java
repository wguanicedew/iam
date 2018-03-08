package it.infn.mw.iam.core.oauth;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        List<String> groupNames =
            info.getGroups().stream().map(IamGroup::getName).collect(Collectors.toList());
        return groupNames.toArray(new String[0]);

      default:
        return null;
    }
  }

}
