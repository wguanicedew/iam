package it.infn.mw.iam.api.scim.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimMemberRef;
import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSshKey;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@Service
public class SshKeyConverter implements Converter<ScimSshKey, IamSshKey> {

  private final ScimResourceLocationProvider resourceLocationProvider;
  private final IamAccountRepository accountRepository;

  @Autowired
  public SshKeyConverter(IamAccountRepository accountRepository,
      ScimResourceLocationProvider resourceLocationProvider) {

    this.accountRepository = accountRepository;
    this.resourceLocationProvider = resourceLocationProvider;
  }

  @Override
  public IamSshKey fromScim(ScimSshKey scim) {

    IamSshKey sshKey = new IamSshKey();
    sshKey.setLabel(scim.getDisplay());
    sshKey.setFingerprint(scim.getFingerprint());
    sshKey.setValue(scim.getValue());

    if (scim.isPrimary() != null) {
      sshKey.setPrimary(scim.isPrimary());
    } else {
      sshKey.setPrimary(false);
    }

    if (scim.getAccountRef() != null) {
      sshKey.setAccount(getAccount(scim.getAccountRef().getValue()));
    } else {
      sshKey.setAccount(null);
    }

    return sshKey;
  }

  @Override
  public ScimSshKey toScim(IamSshKey entity) {

    ScimSshKey.Builder builder = ScimSshKey.builder()
      .display(entity.getLabel())
      .primary(entity.isPrimary())
      .value(entity.getValue());

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
