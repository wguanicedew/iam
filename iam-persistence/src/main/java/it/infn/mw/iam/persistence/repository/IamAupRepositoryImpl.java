package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import it.infn.mw.iam.persistence.model.IamAup;

public class IamAupRepositoryImpl implements IamAupRepositoryCustom {

  private static final String DEFAULT_AUP_NAME = "default-aup";

  @Autowired
  IamAupRepository repo;

  @Override
  public Optional<IamAup> findDefaultAup() {
    return repo.findByName(DEFAULT_AUP_NAME);
  }

  @Override
  public IamAup saveDefaultAup(IamAup aup) {
    aup.setName(DEFAULT_AUP_NAME);
    return repo.save(aup);
  }

}
