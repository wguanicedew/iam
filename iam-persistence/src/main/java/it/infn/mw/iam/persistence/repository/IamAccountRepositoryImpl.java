package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamSamlId;

@Component
public class IamAccountRepositoryImpl implements IamAccountRepositoryCustom {

  @Autowired
  IamAccountRepository repo;

  @Override
  public Optional<IamAccount> findBySamlId(IamSamlId samlId) {
    return repo.findBySamlId(samlId.getIdpId(), samlId.getAttributeId(), 
        samlId.getUserId());
  }
}
