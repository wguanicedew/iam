package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamAccount;

public interface IamAccountRespository
  extends PagingAndSortingRepository<IamAccount, Long> {

  Optional<IamAccount> findByUuid(@Param("uuid") String uuid);

  Optional<IamAccount> findByUsername(@Param("username") String username);

  @Query("select a from IamAccount a join a.samlIds si where si.idpId = :idpId and si.userId = :subject")
  Optional<IamAccount> findBySamlId(@Param("idpId") String idpId,
    @Param("subject") String subject);

  @Query("select a from IamAccount a join a.oidcIds oi where oi.issuer = :issuer and oi.subject = :subject")
  Optional<IamAccount> findByOidcId(@Param("issuer") String issuer,
    @Param("subject") String subject);
  
  @Query("select a from IamAccount a join a.userInfo ui where ui.email = :emailAddress")
  Optional<IamAccount> findByEmail(@Param("emailAddress") String emailAddress);

  
}
