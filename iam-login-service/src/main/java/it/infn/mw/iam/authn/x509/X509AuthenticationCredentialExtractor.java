package it.infn.mw.iam.authn.x509;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

public interface X509AuthenticationCredentialExtractor {

  Optional<IamX509AuthenticationCredential> extractX509Credential(HttpServletRequest request);
}
