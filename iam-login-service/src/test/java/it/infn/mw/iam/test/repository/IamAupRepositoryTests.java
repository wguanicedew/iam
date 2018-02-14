package it.infn.mw.iam.test.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import it.infn.mw.iam.IamLoginService;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.test.api.aup.AupTestSupport;
import it.infn.mw.iam.test.util.DateEqualModulo1Second;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class IamAupRepositoryTests extends AupTestSupport{

  @Autowired
  EntityManager em;

  @Autowired
  IamAupRepository aupRepo;
  
  @Test
  public void defaultAupIsNotDefinedAtStartup() {

    Optional<IamAup> aup = aupRepo.findByName(DEFAULT_AUP_NAME);
    assertThat(aup.isPresent(), is(false));
  }

  @Test
  public void aupCreationWorks() {
  
    IamAup aup = buildDefaultAup();
    Date creationTime = aup.getCreationTime();    
    aupRepo.save(aup);

    aup = aupRepo.findByName(DEFAULT_AUP_NAME)
      .orElseThrow(() -> new AssertionError("Expected aup not found in repository"));
    
    assertThat(aup.getName(), equalTo(DEFAULT_AUP_NAME));
    assertThat(aup.getText(), equalTo(DEFAULT_AUP_TEXT));
    assertThat(aup.getDescription(), equalTo(DEFAULT_AUP_DESC));
    assertThat(aup.getCreationTime(), new DateEqualModulo1Second(creationTime));
    assertThat(aup.getLastUpdateTime(), new DateEqualModulo1Second(creationTime));
    assertThat(aup.getSignatureValidityInDays(), equalTo(365L));
    
  }
  
  @Test
  public void aupRemovalWorks() {
    
    IamAup aup = buildDefaultAup();
    
    aupRepo.save(aup);

    aup = aupRepo.findByName(DEFAULT_AUP_NAME)
      .orElseThrow(() -> new AssertionError("Expected aup not found in repository"));
    
    aupRepo.delete(aup);
    
    assertThat(aupRepo.findByName(DEFAULT_AUP_NAME).isPresent(), is(false)); 
  }



}
