package it.infn.mw.iam.persistence.repository;



import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.model.IamAupSignature;

public interface IamAupSignatureRepository
    extends PagingAndSortingRepository<IamAupSignature, Long>, IamAupSignatureRepositoryCustom {

  Optional<IamAupSignature> findByAupAndAccount(IamAup aup, IamAccount account);
  
}
