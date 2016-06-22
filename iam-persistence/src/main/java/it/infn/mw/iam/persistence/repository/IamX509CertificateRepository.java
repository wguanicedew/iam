package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import it.infn.mw.iam.persistence.model.IamX509Certificate;

public interface IamX509CertificateRepository
  extends CrudRepository<IamX509Certificate, Long> {

  Optional<IamX509Certificate> findByCertificate(
	@Param("Certificate") String certificate);

  Optional<IamX509Certificate> findByCertificateAndLabelAndCertificateSubject(
	@Param("Certificate") String certificate, @Param("Label") String label,
	@Param("CertificateSubject") String certificateSubject);
}
