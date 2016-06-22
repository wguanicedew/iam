package it.infn.mw.iam.api.scim.converter;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.exception.ScimValidationException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimX509Certificate;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamX509Certificate;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class X509CertificateConverter
    implements Converter<ScimX509Certificate, IamX509Certificate> {

  /**
   * <ul>
   * <li>scim.value => certificate</li>
   * <li>scim.display => label</li>
   * <li>scim.primary => primary</li>
   * <li>scim.accountRef => uuid => load account from persistence</li>
   * <li>scim.certificateSubject => must be extract from certificate</li>
   * </ul>
   */

  private final ScimResourceLocationProvider resourceLocationProvider;
  private final IamAccountRepository accountRepository;

  @Autowired
  public X509CertificateConverter(IamAccountRepository accountRepository,
      ScimResourceLocationProvider resourceLocationProvider) {

    this.accountRepository = accountRepository;
    this.resourceLocationProvider = resourceLocationProvider;
  }

  @Override
  public IamX509Certificate fromScim(ScimX509Certificate scim) {

    IamX509Certificate cert = new IamX509Certificate();

    cert.setCertificate(scim.getValue());
    cert.setLabel(scim.getDisplay());

    if (scim.isPrimary() != null) {
      cert.setPrimary(scim.isPrimary());
    } else {
      cert.setPrimary(false);
    }

    if (scim.getAccountRef() != null) {
      cert.setAccount(getAccount(scim.getAccountRef().getValue()));
    } else {
      cert.setAccount(null);
    }

    cert.setCertificateSubject(getX509Certificate(scim.getValue()).getSubjectDN().getName());

    return cert;
  }

  @Override
  public ScimX509Certificate toScim(IamX509Certificate entity) {

    return ScimX509Certificate.builder().primary(entity.isPrimary()).display(entity.getLabel())
        .value(entity.getCertificate())
        .accountRef(ScimMemberRef.builder().display(entity.getAccount().getUsername())
            .value(entity.getAccount().getUuid())
            .ref(resourceLocationProvider.userLocation(entity.getAccount().getUuid())).build())
        .build();
  }

  private IamAccount getAccount(String uuid) {

    return accountRepository.findByUuid(uuid).orElseThrow(
        () -> new ScimResourceNotFoundException("No account mapped to id '" + uuid + "'"));
  }

  public X509Certificate getX509Certificate(String certValue) throws ScimValidationException {

    try {

      return (X509Certificate) CertificateFactory.getInstance("X.509")
          .generateCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(certValue)));

    } catch (IllegalArgumentException e) {

      throw new ScimValidationException("Certificate invalid, it's not Base64 encoded");

    } catch (CertificateException ce) {

      throw new ScimValidationException(ce.getMessage());
    }

  }
}
