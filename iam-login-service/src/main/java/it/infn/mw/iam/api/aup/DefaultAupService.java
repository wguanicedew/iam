package it.infn.mw.iam.api.aup;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.aup.error.AupNotFoundError;
import it.infn.mw.iam.api.aup.model.AupConverter;
import it.infn.mw.iam.api.aup.model.AupDTO;
import it.infn.mw.iam.audit.events.aup.AupCreatedEvent;
import it.infn.mw.iam.audit.events.aup.AupDeletedEvent;
import it.infn.mw.iam.audit.events.aup.AupUpdatedEvent;
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
  private final ApplicationEventPublisher eventPublisher;
  
  @Autowired
  public DefaultAupService(IamAupRepository repo, IamAupSignatureRepository signatureRepo, 
      AupConverter converter, TimeProvider timeProvider, ApplicationEventPublisher eventPublisher) {
    this.repo = repo;
    this.signatureRepo = signatureRepo;
    this.converter = converter;
    this.timeProvider = timeProvider;
    this.eventPublisher = eventPublisher;
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
    
    eventPublisher.publishEvent(new AupCreatedEvent(this, aup));
    
    return aup;
  }

  @Override
  public void deleteAup() {
    IamAup aup = repo.findDefaultAup().orElseThrow(AupNotFoundError::new);
    signatureRepo.deleteByAup(aup);
    repo.delete(aup);
    eventPublisher.publishEvent(new AupDeletedEvent(this, aup));
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
    
    eventPublisher.publishEvent(new AupUpdatedEvent(this, aup));
    return aup;
  }

}
