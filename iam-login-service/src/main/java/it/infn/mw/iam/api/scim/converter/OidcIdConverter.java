package it.infn.mw.iam.api.scim.converter;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;

@Service
public class OidcIdConverter implements Converter<ScimOidcId, IamOidcId> {

  private final IamOidcIdRepository oidcIdRepository;

  @Autowired
  public OidcIdConverter(IamOidcIdRepository oidcIdRepository) {

    this.oidcIdRepository = oidcIdRepository;
  }

  @Override
  public IamOidcId fromScim(ScimOidcId scim) {

    /* Try loading from persistence the OpenID Connect id */
    Optional<IamOidcId> oidcId = oidcIdRepository.findByIssuerAndSubject(scim.issuer, scim.subject);

    if (oidcId.isPresent()) {
      return oidcId.get();
    }

    /* It's a new OpenID Connect id */
    IamOidcId oidcIdNew = new IamOidcId();
    oidcIdNew.setIssuer(scim.issuer);
    oidcIdNew.setSubject(scim.subject);
    oidcIdNew.setAccount(null);
    return oidcIdNew;
  }

  @Override
  public ScimOidcId toScim(IamOidcId entity) {

    ScimOidcId.Builder builder =
        ScimOidcId.builder().issuer(entity.getIssuer()).subject(entity.getSubject());

    return builder.build();
  }

}
