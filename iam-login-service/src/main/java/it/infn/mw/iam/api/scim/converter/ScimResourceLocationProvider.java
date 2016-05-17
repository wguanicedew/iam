package it.infn.mw.iam.api.scim.converter;


public interface ScimResourceLocationProvider {

  public String userLocation(String userId);
  public String groupLocation(String groupId);
  
}
