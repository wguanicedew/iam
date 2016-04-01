package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamAccount;

public interface IamAccountRespository extends PagingAndSortingRepository<IamAccount, Long>{

  Optional<IamAccount> findByUuid(@Param("uuid") String uuid);
  Optional<IamAccount> findByUsername(@Param("username") String username);
  
  @Query("select a from IamAccount a join a.samlAccount sa where sa.idpId = :idpId and sa.userId = :subject")
  Optional<IamAccount> findBySamlAccount(@Param("idpId") String idpId,@Param("subject") String subject);
}
