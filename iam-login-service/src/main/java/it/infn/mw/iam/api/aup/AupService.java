package it.infn.mw.iam.api.aup;

import java.util.Optional;

import it.infn.mw.iam.api.aup.model.AupDTO;
import it.infn.mw.iam.persistence.model.IamAup;

public interface AupService {
  
  Optional<IamAup> findAup();
  
  IamAup saveAup(AupDTO aupDto);
  
  void deleteAup();

}
