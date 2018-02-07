package it.infn.mw.iam.api.aup;

import it.infn.mw.iam.persistence.model.IamAccount;

public interface AUPSignatureCheckService {

  boolean needsAupSignature(IamAccount account);
}
