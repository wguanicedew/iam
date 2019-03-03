package it.infn.mw.iam.api.registration.cern;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile({"cern","mock"})
@Service
@Primary
public class MockCernHrService implements CernHrDBApiService {

  @Override
  public boolean hasValidExperimentParticipation(String personId) {
    
    return true;
  }

}
