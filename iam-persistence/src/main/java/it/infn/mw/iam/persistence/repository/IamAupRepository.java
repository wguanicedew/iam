package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import it.infn.mw.iam.persistence.model.IamAup;

public interface IamAupRepository extends PagingAndSortingRepository<IamAup, Long> {

  String DEFAULT_AUP_NAME = "default-aup";

  Optional<IamAup> findByName(String name);
}
