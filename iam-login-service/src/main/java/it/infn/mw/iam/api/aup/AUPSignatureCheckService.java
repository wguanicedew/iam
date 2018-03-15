package it.infn.mw.iam.api.aup;

import it.infn.mw.iam.persistence.model.IamAccount;

@FunctionalInterface
public interface AUPSignatureCheckService {

  boolean needsAupSignature(IamAccount account);
}
