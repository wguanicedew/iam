package it.infn.mw.iam.api.scim.converter;

/**
 * Given a SCIM user or group id, returns the location URL for the SCIM resource identified by that
 * id
 *
 */
public interface ScimResourceLocationProvider {

  public String userLocation(String userId);

  public String groupLocation(String groupId);

}
