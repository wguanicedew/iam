package it.infn.mw.iam.test.repository;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
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
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.model.IamAupSignature;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.persistence.repository.IamAupSignatureNotFoundError;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;
import it.infn.mw.iam.test.api.aup.AupTestSupport;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {IamLoginService.class})
@Transactional
public class IamAupSignatureRepositoryTests extends AupTestSupport {

  @Autowired
  EntityManager em;

  @Autowired
  IamAupRepository aupRepo;

  @Autowired
  IamAccountRepository accountRepo;

  @Autowired
  IamAupSignatureRepository repo;


  IamAccount findTestAccount() {
    return accountRepo.findByUsername("test")
      .orElseThrow(() -> new AssertionError("Expected test user not found"));
  }

  @Test
  public void noSignatureRecordWithoutDefaultAup() {

    IamAccount testAccount = findTestAccount();

    Optional<IamAupSignature> sig = repo.findSignatureForAccount(testAccount);

    assertThat(sig.isPresent(), is(false));
  }

  @Test(expected = AssertionError.class)
  public void signatureCreationWithoutDefaultAupRaisesException() {
    IamAccount testAccount = findTestAccount();
    try {
      repo.createSignatureForAccount(testAccount);
    } catch (Exception e) {
      assertThat(e.getMessage(),
          equalTo("Default AUP not found in database, cannot create signature"));
      throw e;
    }
  }


  @Test
  public void signatureCreationWorks() {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);

    IamAccount testAccount = findTestAccount();
    Date now = new Date();
    repo.createSignatureForAccount(testAccount);

    IamAupSignature sig = repo.findSignatureForAccount(testAccount)
      .orElseThrow(() -> new AssertionError("Expected signature not found in database"));
    
    assertThat(sig.getAccount(), equalTo(testAccount));
    assertThat(sig.getAup(), equalTo(aup));
    assertThat(testAccount.getAupSignature(), equalTo(sig));

    Instant signatureTime = sig.getSignatureTime().toInstant();
    assertThat(signatureTime.isAfter(now.toInstant()), is(true));
  }

  @Test
  public void signatureCreationCanBeInvokedMultipleTimes() {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);

    IamAccount testAccount = findTestAccount();
    Date now = new Date();
    repo.createSignatureForAccount(testAccount);

    IamAupSignature sig = repo.findSignatureForAccount(testAccount)
      .orElseThrow(() -> new AssertionError("Expected signature not found in database"));

    assertThat(sig.getAccount(), equalTo(testAccount));
    assertThat(sig.getAup(), equalTo(aup));

    Instant signatureTime = sig.getSignatureTime().toInstant();
    assertThat(signatureTime.isAfter(now.toInstant()), is(true));
    
    Instant firstSigTime = sig.getSignatureTime().toInstant();
    repo.createSignatureForAccount(testAccount);
    
    sig = repo.findSignatureForAccount(testAccount)
        .orElseThrow(() -> new AssertionError("Expected signature not found in database"));
    
    signatureTime = sig.getSignatureTime().toInstant();
    assertThat(signatureTime.isAfter(firstSigTime), is(true));
  }

  @Test(expected = IamAupSignatureNotFoundError.class)
  public void signatureUpdateWithoutDefaultAupRaisesException() {
    IamAccount testAccount = findTestAccount();
    try {
      repo.updateSignatureForAccount(testAccount);
    } catch (Exception e) {
      assertThat(e.getMessage(), equalTo("AUP signature not found for user 'test'"));
      throw e;
    }
  }

  @Test(expected = IamAupSignatureNotFoundError.class)
  public void signatureUpdateWithoutSignatureRecordRaisesException() {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);
    IamAccount testAccount = findTestAccount();
    try {
      repo.updateSignatureForAccount(testAccount);
    } catch (Exception e) {
      assertThat(e.getMessage(), equalTo("AUP signature not found for user 'test'"));
      throw e;
    }
  }

  @Test
  public void signatureUpdateUpdatesSignatureTime() {
    IamAup aup = buildDefaultAup();
    aupRepo.save(aup);
    IamAccount testAccount = findTestAccount();

    IamAupSignature sig = repo.createSignatureForAccount(testAccount);
    Instant oldSigTime = sig.getSignatureTime().toInstant();
    sig = repo.updateSignatureForAccount(testAccount);
    assertThat(sig.getSignatureTime().toInstant().isAfter(oldSigTime), is(true));
  }

}
