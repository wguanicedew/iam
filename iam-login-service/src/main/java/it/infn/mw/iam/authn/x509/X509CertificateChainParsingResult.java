package it.infn.mw.iam.authn.x509;

import java.security.cert.X509Certificate;

public class X509CertificateChainParsingResult {

  private final String pemString;
  private final X509Certificate[] chain;

  private X509CertificateChainParsingResult(String pemString, X509Certificate[] chain) {
    this.pemString = pemString;
    this.chain = chain;
  }

  public String getPemString() {
    return pemString;
  }

  public X509Certificate[] getChain() {
    return chain;
  }

  public static X509CertificateChainParsingResult from(String pemString, X509Certificate[] chain){
    return new X509CertificateChainParsingResult(pemString, chain);
  }

}
