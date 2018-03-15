package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamAup;

public interface IamAupRepositoryCustom {

  Optional<IamAup> findDefaultAup();
  IamAup saveDefaultAup(IamAup aup);
}
