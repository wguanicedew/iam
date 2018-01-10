package it.infn.mw.iam.api.aup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.api.aup.error.AupNotFoundError;
import it.infn.mw.iam.api.aup.model.AupConverter;
import it.infn.mw.iam.api.aup.model.AupDTO;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;

@Service
public class DefaultAupService implements AupService {

  private final IamAupRepository repo;
  private final AupConverter converter;

  @Autowired
  public DefaultAupService(IamAupRepository repo, AupConverter converter) {
    this.repo = repo;
    this.converter = converter;
  }

  @Override
  public Optional<IamAup> findAup() {
    return repo.findDefaultAup();
  }

  @Override
  public IamAup saveAup(AupDTO aupDto) {
    IamAup aup = converter.entityFromDto(aupDto);
    repo.saveDefaultAup(aup);
    return aup;
  }

  @Override
  public void deleteAup() {
    IamAup aup = repo.findDefaultAup().orElseThrow(AupNotFoundError::new);
    repo.delete(aup);
  }

}
