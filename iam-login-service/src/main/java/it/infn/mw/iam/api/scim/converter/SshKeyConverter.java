package it.infn.mw.iam.api.scim.converter;

import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.scim.model.ScimSshKey;
import it.infn.mw.iam.persistence.model.IamSshKey;

@Service
public class SshKeyConverter implements Converter<ScimSshKey, IamSshKey> {

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
    
    sshKey.setAccount(null);

    return sshKey;
  }

  @Override
  public ScimSshKey toScim(IamSshKey entity) {

    ScimSshKey.Builder builder = ScimSshKey.builder()
      .display(entity.getLabel())
      .primary(entity.isPrimary())
      .value(entity.getValue())
      .fingerprint(entity.getFingerprint());

    return builder.build();
  }
}
