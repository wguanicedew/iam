package it.infn.mw.iam.api.scim.converter;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.persistence.model.IamOidcId;

@Service
public class OidcIdConverter implements Converter<ScimOidcId, IamOidcId> {

  @Override
  public IamOidcId entityFromDto(ScimOidcId scim) {

    IamOidcId oidcId = new IamOidcId();
    oidcId.setIssuer(scim.getIssuer());
    oidcId.setSubject(scim.getSubject());
    oidcId.setAccount(null);

    return oidcId;
  }

  @Override
  public ScimOidcId dtoFromEntity(IamOidcId entity) {

    ScimOidcId.Builder builder = ScimOidcId.builder()
      .issuer(entity.getIssuer())
      .subject(entity.getSubject());

    return builder.build();
  }
}