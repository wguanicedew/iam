package it.infn.mw.iam.api.aup;

import static it.infn.mw.iam.persistence.repository.IamAupRepository.DEFAULT_AUP_NAME;

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
    return repo.findByName(DEFAULT_AUP_NAME);
  }

  @Override
  public IamAup saveAup(AupDTO aupDto) {
    IamAup aup = converter.entityFromDto(aupDto);
    aup.setName(DEFAULT_AUP_NAME);
    repo.save(aup);
    return aup;
  }

  @Override
  public void deleteAup() {
    IamAup aup = repo.findByName(DEFAULT_AUP_NAME).orElseThrow(AupNotFoundError::new);
    repo.delete(aup);
  }

}
