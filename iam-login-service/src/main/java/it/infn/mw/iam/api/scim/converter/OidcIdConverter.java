package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class OidcIdConverter implements Converter<ScimOidcId, IamOidcId> {

  private final ScimResourceLocationProvider resourceLocationProvider;
  private final IamAccountRepository accountRepository;

  @Autowired
  public OidcIdConverter(ScimResourceLocationProvider resourceLocationProvider,
      IamAccountRepository accountRepository) {

    this.resourceLocationProvider = resourceLocationProvider;
    this.accountRepository = accountRepository;
  }

  @Override
  public IamOidcId fromScim(ScimOidcId scim) {

    IamOidcId oidcId = new IamOidcId();
    oidcId.setIssuer(scim.getIssuer());
    oidcId.setSubject(scim.getSubject());

    if (scim.getAccountRef() != null) {
      oidcId.setAccount(getAccount(scim.getAccountRef().getValue()));
    } else {
      oidcId.setAccount(null);
    }

    return oidcId;
  }

  @Override
  public ScimOidcId toScim(IamOidcId entity) {

    ScimOidcId.Builder builder = ScimOidcId.builder()
      .issuer(entity.getIssuer())
      .subject(entity.getSubject());
    
    if (entity.getAccount() != null) {
      builder.accountRef(ScimMemberRef.builder()
        .display(entity.getAccount().getUsername())
        .value(entity.getAccount().getUuid())
        .ref(resourceLocationProvider.userLocation(entity.getAccount().getUuid()))
        .build());
    }

    return builder.build();
  }

  private IamAccount getAccount(String uuid) {

    return accountRepository.findByUuid(uuid).orElseThrow(
        () -> new ScimResourceNotFoundException("No account mapped to id '" + uuid + "'"));
  }
}
