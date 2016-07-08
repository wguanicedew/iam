package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamX509Certificate;

public interface IamX509CertificateRepository extends CrudRepository<IamX509Certificate, Long> {

  Optional<IamX509Certificate> findByCertificate(@Param("Certificate") String certificate);

  @Query("select c from IamX509Certificate c join c.account a where c.certificate = :certificate and a.uuid = :uuid")
  Optional<IamX509Certificate> findByCertificateAndUuid(@Param("certificate") String certificate,
      @Param("uuid") String uuid);

  Optional<IamX509Certificate> findByCertificateAndLabelAndCertificateSubject(
      @Param("Certificate") String certificate, @Param("Label") String label,
      @Param("CertificateSubject") String certificateSubject);
}
