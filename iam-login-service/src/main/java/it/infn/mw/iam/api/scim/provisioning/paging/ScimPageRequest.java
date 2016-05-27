package it.infn.mw.iam.api.scim.provisioning.paging;


public interface ScimPageRequest {
  
  public int getCount();
  public int getStartIndex();

}
