package it.infn.mw.iam.api.scim.new_updater.builders;

import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.removeOidcId;
import static it.infn.mw.iam.api.scim.new_updater.AccountUpdater.removeSamlId;

import java.util.Collection;
import java.util.Collections;

import it.infn.mw.iam.api.scim.new_updater.DefaultUpdater;
import it.infn.mw.iam.api.scim.new_updater.Updater;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamOidcId;
import it.infn.mw.iam.persistence.model.IamSamlId;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

public class Removers extends BuilderSupport {

  public Removers(IamAccountRepository repo, IamAccount account) {
    super(repo, account);
  }

  public Updater oidcId(Collection<IamOidcId> toBeRemoved) {
    Collection<IamOidcId> oidcIds = account.getOidcIds();

    return new DefaultUpdater<Collection<IamOidcId>>(removeOidcId, oidcIds::removeAll, toBeRemoved,
        i -> !Collections.disjoint(oidcIds, i));
  }

  public Updater samlId(Collection<IamSamlId> toBeRemoved) {
    Collection<IamSamlId> samlIds = account.getSamlIds();

    return new DefaultUpdater<Collection<IamSamlId>>(removeSamlId, samlIds::removeAll, toBeRemoved,
        i -> !Collections.disjoint(samlIds, i));
  }
}
