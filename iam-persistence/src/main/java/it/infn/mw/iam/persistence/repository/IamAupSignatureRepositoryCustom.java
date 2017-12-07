package it.infn.mw.iam.persistence.repository;

import java.util.Optional;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAupSignature;

public interface IamAupSignatureRepositoryCustom {
  
  Optional<IamAupSignature> findSignatureForAccount(IamAccount account);
  
  IamAupSignature createSignatureForAccount(IamAccount account);
  
  IamAupSignature updateSignatureForAccount(IamAccount account);

}
