package it.infn.mw.iam.authn.x509;

import java.security.cert.X509Certificate;

public class IamX509AuthenticationCredential {

  private final String subject;
  private final String issuer;

  private final X509Certificate[] certificateChain;
  
  private final X509CertificateVerificationResult verificationStatus;
  
  private IamX509AuthenticationCredential(Builder builder) {
    this.subject = builder.subject;
    this.issuer = builder.issuer;
    this.certificateChain = builder.certificateChain;
    this.verificationStatus = builder.verificationStatus;
  }

  public static class Builder {
    private String subject;
    private String issuer;
    private X509Certificate[] certificateChain;
    private X509CertificateVerificationResult verificationStatus;
    
    public Builder subject(String subject){
      this.subject = subject;
      return this;
    }
    
    public Builder issuer(String issuer){
      this.issuer = issuer;
      return this;
    }
    
    public Builder certificateChain(X509Certificate[] chain){
      this.certificateChain = chain;
      return this;
    }
    
    public Builder verificationStatus(X509CertificateVerificationResult s){
      this.verificationStatus = s;
      return this;
    }
    
    public IamX509AuthenticationCredential build(){
      return new IamX509AuthenticationCredential(this);
    }
  }

  public String getSubject() {
    return subject;
  }

  public String getIssuer() {
    return issuer;
  }

  public X509Certificate[] getCertificateChain() {
    return certificateChain;
  }

  public X509CertificateVerificationResult getVerificationStatus() {
    return verificationStatus;
  }
}
