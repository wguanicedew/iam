package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamAuthority;

public interface IamAuthoritiesRepository
  extends CrudRepository<IamAuthority, Long> {

  Optional<IamAuthority> findByAuthority(@Param("authority") String authority);

}
