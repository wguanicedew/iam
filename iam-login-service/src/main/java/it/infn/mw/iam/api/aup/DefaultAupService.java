package it.infn.mw.iam.api.aup;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.aup.error.AupNotFoundError;
import it.infn.mw.iam.api.aup.model.AupConverter;
import it.infn.mw.iam.api.aup.model.AupDTO;
import it.infn.mw.iam.core.time.TimeProvider;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;

@Service
public class DefaultAupService implements AupService {

  private final IamAupRepository repo;
  private final IamAupSignatureRepository signatureRepo;
  private final AupConverter converter;

  private final TimeProvider timeProvider;
  
  @Autowired
  public DefaultAupService(IamAupRepository repo, IamAupSignatureRepository signatureRepo, 
      AupConverter converter, TimeProvider timeProvider) {
    this.repo = repo;
    this.signatureRepo = signatureRepo;
    this.converter = converter;
    this.timeProvider = timeProvider;
  }

  @Override
  public Optional<IamAup> findAup() {
    return repo.findDefaultAup();
  }

  @Override
  public IamAup saveAup(AupDTO aupDto) {
    IamAup aup = converter.entityFromDto(aupDto);
    long currentTimeMillis = timeProvider.currentTimeMillis(); 
    
    Date now = new Date(currentTimeMillis);
    aup.setCreationTime(now);
    aup.setLastUpdateTime(now);
    
    repo.saveDefaultAup(aup);
    return aup;
  }

  @Override
  public void deleteAup() {
    IamAup aup = repo.findDefaultAup().orElseThrow(AupNotFoundError::new);
    signatureRepo.deleteByAup(aup);
    repo.delete(aup);
  }

  @Override
  public IamAup updateAup(AupDTO aupDto) {
    
    long currentTimeMillis = timeProvider.currentTimeMillis(); 
    Date now = new Date(currentTimeMillis);
    
    IamAup aup = repo.findDefaultAup().orElseThrow(AupNotFoundError::new);
    
    aup.setLastUpdateTime(now);
    aup.setDescription(aupDto.getDescription());
    aup.setText(aupDto.getText());
    aup.setSignatureValidityInDays(aupDto.getSignatureValidityInDays());
    repo.save(aup);
    
    return aup;
  }

}
