package it.infn.mw.iam.api.scim.model;

import org.springframework.http.MediaType;

public interface ScimConstants {

  final MediaType SCIM_MEDIA_TYPE = new MediaType("application", "scim+json");
  final String SCIM_CONTENT_TYPE = "application/scim+json";
  final String INDIGO_USER_SCHEMA = "urn:indigo-dc:scim:schemas:IndigoUser";


}
