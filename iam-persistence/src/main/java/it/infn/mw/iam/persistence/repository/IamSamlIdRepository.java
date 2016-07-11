package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamSamlId;

public interface IamSamlIdRepository extends CrudRepository<IamSamlId, Long> {

  Optional<IamSamlId> findByIdpIdAndUserId(@Param("idpId") String idpId, @Param("userId") String userId);

}
