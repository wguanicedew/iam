package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class SamlIdConverter implements Converter<ScimSamlId, IamSamlId> {

  private final ScimResourceLocationProvider resourceLocationProvider;
  private final IamAccountRepository accountRepository;

  @Autowired
  public SamlIdConverter(IamAccountRepository accountRepository,
      ScimResourceLocationProvider resourceLocationProvider) {

    this.accountRepository = accountRepository;
    this.resourceLocationProvider = resourceLocationProvider;
  }

  @Override
  public IamSamlId fromScim(ScimSamlId scim) {

    IamSamlId samlId = new IamSamlId();
    samlId.setIdpId(scim.getIdpId());
    samlId.setUserId(scim.getUserId());

    if (scim.getAccountRef() != null) {
      samlId.setAccount(getAccount(scim.getAccountRef().getValue()));
    } else {
      samlId.setAccount(null);
    }

    return samlId;
  }

  @Override
  public ScimSamlId toScim(IamSamlId entity) {

    ScimSamlId.Builder builder =
        ScimSamlId.builder().idpId(entity.getIdpId()).userId(entity.getUserId());

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
