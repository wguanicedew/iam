package it.infn.mw.iam.api.group.find;

import org.springframework.data.domain.Pageable;

import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimListResponse;

public interface FindGroupService {

  ScimListResponse<ScimGroup> findGroupByName(String name);

  ScimListResponse<ScimGroup> findGroupByLabel(String labelName, String labelValue,
      Pageable pageable);

}
