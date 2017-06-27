package it.infn.mw.iam.authn.x509;

import java.security.cert.X509Certificate;

public interface X509CertificateChainParser {

  X509Certificate[] parseChainFromString(String certChain);
}
