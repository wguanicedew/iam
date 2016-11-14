package it.infn.mw.iam.api.scim.updater.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.OidcIdConverter;
import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimOidcId;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamOidcIdRepository;

@Component
public class OpenIDConnectAccountUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private IamAccountRepository accountRepository;
  @Autowired
  private IamOidcIdRepository oidcIdRepository;

  @Autowired
  private OidcIdConverter oidcIdConverter;

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getIndigoUser());
    Preconditions.checkNotNull(user.getIndigoUser().getOidcIds());
    Preconditions.checkArgument(!user.getIndigoUser().getOidcIds().isEmpty());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    validate(account, user);

    return user.getIndigoUser()
      .getOidcIds()
      .stream()
      .map(oidcId -> addOpenIdConnectAccount(account, oidcId))
      .filter(result -> result)
      .count() > 1;
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    validate(account, user);

    return user.getIndigoUser()
        .getOidcIds()
        .stream()
        .map(oidcId -> removeOpenIdConnectAccount(account, oidcId))
        .filter(result -> result)
        .count() > 1;
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Replace OpenID Connect account is not supported");
  }

  private boolean addOpenIdConnectAccount(IamAccount account, ScimOidcId oidcId) {

    Preconditions.checkNotNull(oidcId, "Add OpenID account: null OpenID account");
    Preconditions.checkNotNull(oidcId.getIssuer(), "Add OpenID account: null issuer");
    Preconditions.checkNotNull(oidcId.getSubject(), "Add OpenID account: null subject");

    Optional<IamAccount> oidcAccount =
        accountRepository.findByOidcId(oidcId.getIssuer(), oidcId.getSubject());

    if (oidcAccount.isPresent()) {

      if (!oidcAccount.get().equals(account)) {
        throw new ScimResourceExistsException(
            String.format("OpenID account (%s,%s) is already mapped to another user",
                oidcId.getIssuer(), oidcId.getSubject()));
      }
      return false;
    }

    IamOidcId iamOidcId = oidcIdConverter.fromScim(oidcId);
    iamOidcId.setAccount(account);

    oidcIdRepository.save(iamOidcId);
    account.getOidcIds().add(iamOidcId);

    return true;
  }

  private boolean removeOpenIdConnectAccount(IamAccount account, ScimOidcId oidcId) {

    Preconditions.checkNotNull(oidcId, "Remove OpenID account: null OpenID account");

    Optional<IamOidcId> toRemove = account.getOidcIds()
      .stream()
      .filter(o -> o.getIssuer().equals(oidcId.getIssuer())
          && o.getSubject().equals(oidcId.getSubject()))
      .findFirst();

    if (!toRemove.isPresent()) {

      throw new ScimResourceNotFoundException(String.format(
          "No Open ID connect account found for (%s,%s)", oidcId.getSubject(), oidcId.getIssuer()));
    }

    oidcIdRepository.delete(toRemove.get());
    account.getOidcIds().remove(toRemove.get());
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getIndigoUser() != null && user.getIndigoUser().getOidcIds() != null
        && !user.getIndigoUser().getOidcIds().isEmpty();  
  }
}
