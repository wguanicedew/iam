package it.infn.mw.iam.api.aup;

import static java.util.Objects.isNull;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamAup;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.persistence.repository.IamAupSignatureRepository;

@Service
public class DefaultAupSignatureCheckService implements AUPSignatureCheckService {

  public static final Logger LOG = LoggerFactory.getLogger(DefaultAupSignatureCheckService.class);

  final IamAupRepository aupRepo;
  final IamAupSignatureRepository signatureRepo;

  @Autowired
  public DefaultAupSignatureCheckService(IamAupRepository aupRepo,
      IamAupSignatureRepository signatureRepo) {
    this.aupRepo = aupRepo;
    this.signatureRepo = signatureRepo;
  }

  @Override
  public boolean needsAupSignature(IamAccount account) {
    Optional<IamAup> aup = aupRepo.findDefaultAup();

    if (!aup.isPresent()) {
      LOG.debug("AUP signature not needed for account '{}': AUP is not defined",
          account.getUsername());
      return false;
    }

    if (isNull(account.getAupSignature())) {
      LOG.debug("AUP signature needed for account '{}': no signature record found for user",
          account.getUsername());
      return true;
    }

    Date signatureTime = account.getAupSignature().getSignatureTime();
    Date aupLastModifiedTime = aup.get().getLastUpdateTime();

    boolean signatureNeeded = signatureTime.compareTo(aupLastModifiedTime) < 0;
    String signatureNeededString = (signatureNeeded ? "needed" : "not needed");
    LOG.debug(
        "AUP signature {} for account '{}': AUP signature time '{}', AUP last modified time '{}'",
        signatureNeededString, account.getUsername(), signatureTime, aupLastModifiedTime);

    return signatureNeeded;
  }

}
