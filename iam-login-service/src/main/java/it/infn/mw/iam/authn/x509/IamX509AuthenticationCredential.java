package it.infn.mw.iam.authn.x509;

import java.security.cert.X509Certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.infn.mw.iam.persistence.model.IamX509Certificate;

public class IamX509AuthenticationCredential {
  
  private final String subject;
  private final String issuer;

  @JsonIgnore
  private final X509Certificate[] certificateChain;
  
  private final String certificateChainPemString;
  
  @JsonIgnore
  private final X509CertificateVerificationResult verificationResult;
  
  private IamX509AuthenticationCredential(Builder builder) {
    this.subject = builder.subject;
    this.issuer = builder.issuer;
    this.certificateChain = builder.certificateChain;
    this.verificationResult = builder.verificationResult;
    this.certificateChainPemString  = builder.certificateChainPemString;
  }

  public static class Builder {
    private String subject;
    private String issuer;
    private X509Certificate[] certificateChain;
    private String certificateChainPemString;
    private X509CertificateVerificationResult verificationResult;
    
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
    
    public Builder verificationResult(X509CertificateVerificationResult s){
      this.verificationResult = s;
      return this;
    }
    
    public Builder certificateChainPemString(String ccps){
      this.certificateChainPemString =ccps;
      return this;
    }
    
    public IamX509AuthenticationCredential build(){
      return new IamX509AuthenticationCredential(this);
    }
  }

  public IamX509Certificate asIamX509Certificate(){
    IamX509Certificate cert = new IamX509Certificate();
    cert.setSubjectDn(getSubject());
    cert.setIssuerDn(getIssuer());
    cert.setCertificate(getCertificateChainPemString());
    return cert;
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
  
  public String getCertificateChainPemString() {
    return certificateChainPemString;
  }
  public X509CertificateVerificationResult getVerificationResult() {
    return verificationResult;
  }
  
  public boolean failedVerification(){
    return verificationResult.failedVerification();
  }
  
  public String verificationError(){
    return verificationResult.error().orElse("X.509 credential is valid");
  }
  
  public static Builder builder(){
    return new Builder();
  }
}
