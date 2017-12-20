package it.infn.mw.iam.persistence.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.model.IamAupSignature;

@Component
public class IamAupSignatureRepositoryImpl implements IamAupSignatureRepositoryCustom {

  @Autowired
  IamAupSignatureRepository repo;

  @Autowired
  IamAupRepository aupRepo;
  
  @Override
  public Optional<IamAupSignature> findSignatureForAccount(IamAccount account) {

    Optional<IamAup> aup = aupRepo.findByName(IamAupRepository.DEFAULT_AUP_NAME);

    if (!aup.isPresent()) {
      return Optional.empty();
    }

    return repo.findByAupAndAccount(aup.get(), account);
  }

  private IamAupSignature createSignatureForAccount(IamAup aup, IamAccount account) {
    IamAupSignature newSignature = new IamAupSignature();
    newSignature.setAccount(account);
    newSignature.setAup(aup);
    account.setAupSignature(newSignature);
    return newSignature;
  }

  @Override
  public IamAupSignature createSignatureForAccount(IamAccount account, Date currentTime) {
    IamAup aup = aupRepo.findByName(IamAupRepository.DEFAULT_AUP_NAME)
      .orElseThrow(() -> new IllegalStateException(
          "Default AUP not found in database, cannot create signature"));

    IamAupSignature signature = repo.findSignatureForAccount(account)
      .orElseGet(() -> createSignatureForAccount(aup, account));

    signature.setSignatureTime(currentTime);
    
    repo.save(signature);
    return signature;
  }

  @Override
  public IamAupSignature updateSignatureForAccount(IamAccount account, Date currentTime) {

    IamAupSignature signature = findSignatureForAccount(account)
      .orElseThrow(() -> new IamAupSignatureNotFoundError(account));


    signature.setSignatureTime(currentTime);
    repo.save(signature);

    return signature;
  }

}
