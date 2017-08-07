package it.infn.mw.iam.authn.x509;
@FunctionalInterface
public interface X509CertificateChainParser {
  
  X509CertificateChainParsingResult parseChainFromString(String pemCertificateChain);
  
}
