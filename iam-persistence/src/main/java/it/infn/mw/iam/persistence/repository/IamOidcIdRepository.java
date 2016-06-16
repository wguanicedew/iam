package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamOidcId;

public interface IamOidcIdRepository extends CrudRepository<IamOidcId, Long> {

  Optional<IamOidcId> findByIssuerAndSubject(@Param("issuer") String issuer,
    @Param("subject") String subject);

}
