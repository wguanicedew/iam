package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamGroup;

public interface IamGroupRepository
  extends PagingAndSortingRepository<IamGroup, Long> {

  Optional<IamGroup> findByUuid(@Param("uuid") String uuid);

  Optional<IamGroup> findByName(@Param("name") String name);

}
