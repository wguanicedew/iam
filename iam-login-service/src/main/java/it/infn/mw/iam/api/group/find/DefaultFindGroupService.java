package it.infn.mw.iam.api.group.find;

import static it.infn.mw.iam.api.utils.FindUtils.responseFromPage;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.converter.GroupConverter;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimListResponse.ScimListResponseBuilder;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Service
public class DefaultFindGroupService implements FindGroupService {

  final GroupConverter converter;
  final IamGroupRepository repo;

  @Autowired
  public DefaultFindGroupService(GroupConverter converter, IamGroupRepository repo) {
    this.converter = converter;
    this.repo = repo;
  }

  @Override
  public ScimListResponse<ScimGroup> findGroupByName(String name) {
    Optional<IamGroup> maybeGroup = repo.findByName(name);

    ScimListResponseBuilder<ScimGroup> builder = ScimListResponse.builder();

    maybeGroup.ifPresent(a -> builder.singleResource(converter.dtoFromEntity(a)));
    return builder.build();
  }

  @Override
  public ScimListResponse<ScimGroup> findGroupByLabel(String labelName, String labelValue,
      Pageable pageable) {

    Page<IamGroup> results = repo.findByLabelNameAndValue(labelName, labelValue, pageable);
    return responseFromPage(results, converter, pageable);
  }

}
