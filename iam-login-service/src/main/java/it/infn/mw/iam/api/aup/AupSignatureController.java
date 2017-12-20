package it.infn.mw.iam.api.aup;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.aup.error.AupSignatureNotFoundError;
import it.infn.mw.iam.api.aup.model.AupSignatureConverter;
import it.infn.mw.iam.api.aup.model.AupSignatureDTO;
import it.infn.mw.iam.api.common.ErrorDTO;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAupSignature;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;

@RestController
public class AupSignatureController {

  private final AupSignatureConverter signatureConverter;
  private final AccountUtils accountUtils;
  private final IamAupSignatureRepository signatureRepo;

  @Autowired
  public AupSignatureController(AupSignatureConverter conv, AccountUtils utils,
      IamAupSignatureRepository signatureRepo) {
    this.signatureConverter = conv;
    this.accountUtils = utils;
    this.signatureRepo = signatureRepo;
  }

  @RequestMapping(value = "/iam/aup/signature", method = RequestMethod.POST)
  @PreAuthorize("hasRole('USER')")
  @ResponseStatus(code = HttpStatus.CREATED)
  public void signAup() {
    IamAccount account = accountUtils.getAuthenticatedUserAccount()
      .orElseThrow(() -> new IllegalStateException("Account not found for authenticated user"));

    signatureRepo.createSignatureForAccount(account, new Date());
  }

  @RequestMapping(value = "/iam/aup/signature", method = RequestMethod.GET)
  @PreAuthorize("hasRole('USER')")
  public AupSignatureDTO getSignature() {
    IamAccount account = accountUtils.getAuthenticatedUserAccount()
      .orElseThrow(() -> new IllegalStateException("Account not found for authenticated user"));


    IamAupSignature sig = signatureRepo.findSignatureForAccount(account)
      .orElseThrow(() -> new AupSignatureNotFoundError(account));
    
    return signatureConverter.dtoFromEntity(sig);
  }
  
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  @ExceptionHandler(AupSignatureNotFoundError.class)
  public ErrorDTO notFoundError(Exception ex) {
    return ErrorDTO.fromString(ex.getMessage());
  }
}
