package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamSshKey;

public interface IamSshKeyRepository extends CrudRepository<IamSshKey, Long> {

  Optional<IamSshKey> findByFingerprint(@Param("fingerprint") String fingerprint);

}
