package it.infn.mw.iam.api.account.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import it.infn.mw.iam.api.common.OffsetPageable;
import it.infn.mw.iam.api.common.PagedResourceService;
import it.infn.mw.iam.persistence.model.IamGroup;
import it.infn.mw.iam.persistence.repository.IamGroupRepository;

@Service
public class DefaultPagedGroupsService implements PagedResourceService<IamGroup> {

  @Autowired
  private IamGroupRepository groupRepository;

  @Override
  public Page<IamGroup> getPage(OffsetPageable op) {
    return groupRepository.findAll(op);
  }

  @Override
  public long count() {
    return groupRepository.count();
  }

  @Override
  public Page<IamGroup> getPage(OffsetPageable op, String filter) {

    filter = String.format("%%%s%%", filter);
    return groupRepository.findByFilter(filter, op);
  }

  @Override
  public long count(String filter) {

    filter = String.format("%%%s%%", filter);
    return groupRepository.countByFilter(filter);
  }

}
