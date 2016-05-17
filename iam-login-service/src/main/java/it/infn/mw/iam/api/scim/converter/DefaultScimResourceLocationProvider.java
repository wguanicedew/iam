package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DefaultScimResourceLocationProvider
  implements ScimResourceLocationProvider {

  public static final String SCIM_API_ENDPOINT = "/scim";

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Override
  public String userLocation(String userId) {

    return String.format("%s%s/Users/%s", baseUrl, SCIM_API_ENDPOINT, userId);
  }

  @Override
  public String groupLocation(String groupId) {

    return String.format("%s%s/Groups/%s", baseUrl, SCIM_API_ENDPOINT,
      groupId);
  }

}
