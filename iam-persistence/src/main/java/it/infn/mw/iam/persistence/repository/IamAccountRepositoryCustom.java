package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

public interface IamAccountRepositoryCustom {
  
  Optional<IamAccount> findBySamlId(IamSamlId samlId);

}
