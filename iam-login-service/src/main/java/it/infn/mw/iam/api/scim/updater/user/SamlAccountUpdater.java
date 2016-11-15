package it.infn.mw.iam.api.scim.updater.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import it.infn.mw.iam.api.scim.converter.SamlIdConverter;
import it.infn.mw.iam.api.scim.exception.ScimPatchOperationNotSupported;
import it.infn.mw.iam.api.scim.exception.ScimResourceExistsException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimSamlId;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamSamlIdRepository;

@Component
public class SamlAccountUpdater implements Updater<IamAccount, ScimUser> {

  @Autowired
  private IamAccountRepository accountRepository;
  @Autowired
  private IamSamlIdRepository samlIdRepository;

  @Autowired
  private SamlIdConverter samlIdConverter;

  private void validate(IamAccount account, ScimUser user) {

    Preconditions.checkNotNull(account);
    Preconditions.checkNotNull(user);
    Preconditions.checkNotNull(user.getIndigoUser());
    Preconditions.checkNotNull(user.getIndigoUser().getSamlIds());
    Preconditions.checkArgument(!user.getIndigoUser().getSamlIds().isEmpty());
  }

  @Override
  public boolean add(IamAccount account, ScimUser user) {

    validate(account, user);

    return user.getIndigoUser()
      .getSamlIds()
      .stream()
      .map(samlId -> addSamlAccount(account, samlId))
      .filter(result -> result)
      .count() > 1;
  }

  @Override
  public boolean remove(IamAccount account, ScimUser user) {

    validate(account, user);

    return user.getIndigoUser()
        .getSamlIds()
        .stream()
        .map(samlId -> removeSamlAccount(account, samlId))
        .filter(result -> result)
        .count() > 1;
  }

  @Override
  public boolean replace(IamAccount account, ScimUser user) {

    throw new ScimPatchOperationNotSupported("Replace SAML account is not supported");
  }

  private boolean addSamlAccount(IamAccount account, ScimSamlId samlId) {

    Preconditions.checkNotNull(samlId, "Add Saml Id: null Saml Id");
    Preconditions.checkNotNull(samlId.getIdpId(), "Add Saml Id: null idpId");
    Preconditions.checkNotNull(samlId.getUserId(), "Add Saml Id: null userId");

    Optional<IamAccount> samlAccount =
        accountRepository.findBySamlId(samlId.getIdpId(), samlId.getUserId());

    if (samlAccount.isPresent()) {

      if (!samlAccount.get().equals(account)) {

        throw new ScimResourceExistsException(
            String.format("Saml account (%s,%s) is already mapped to another user",
                samlId.getIdpId(), samlId.getUserId()));
      }
      return false;
    }

    IamSamlId samlIdToCreate = samlIdConverter.fromScim(samlId);
    samlIdToCreate.setAccount(account);
    samlIdRepository.save(samlIdToCreate);
    account.getSamlIds().add(samlIdToCreate);
    return true;
  }

  private boolean removeSamlAccount(IamAccount account, ScimSamlId samlId) {

    Preconditions.checkNotNull(samlId, "Remove Saml Id: null saml id");

    IamSamlId toRemove =
        account.getSamlIds()
          .stream()
          .filter(s -> s.getIdpId().equals(samlId.getIdpId())
              && s.getUserId().equals(samlId.getUserId()))
          .findFirst()
          .orElseThrow(() -> new ScimResourceNotFoundException(
              String.format("User %s has no (%s,%s) saml account to remove!", account.getUsername(),
                  samlId.getIdpId(), samlId.getUserId())));

    samlIdRepository.delete(toRemove);
    account.getSamlIds().remove(toRemove);
    return true;
  }

  @Override
  public boolean accept(ScimUser user) {

    return user.getIndigoUser() != null && user.getIndigoUser().getSamlIds() != null
        && !user.getIndigoUser().getSamlIds().isEmpty();  
  }
}
