package it.infn.mw.iam.test.ext_authn.x509;

import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.security.cert.X509Certificate;

import org.junit.Test;

import it.infn.mw.iam.authn.x509.IamX509AuthenticationCredential;
import it.infn.mw.iam.authn.x509.X509CertificateVerificationResult;

public class X509AuthenticationCredentialTests {

  public static final String TEST_SUBJECT = "Test subject";
  public static final String TEST_ISSUER = "Test issuer";
  public static final String VERIFICATION_ERROR = "Verification error";

  @Test
  public void testCredentialCreation() {
    IamX509AuthenticationCredential.Builder builder = new IamX509AuthenticationCredential.Builder();
    IamX509AuthenticationCredential cred = builder.subject(TEST_SUBJECT)
      .issuer(TEST_ISSUER)
      .verificationStatus(X509CertificateVerificationResult.failed(VERIFICATION_ERROR))
      .certificateChain(new X509Certificate[] {})
      .build();

    assertThat(cred.getSubject(), equalTo(TEST_SUBJECT));
    assertThat(cred.getIssuer(), equalTo(TEST_ISSUER));
    assertThat(cred.getVerificationStatus().status(),
        is(X509CertificateVerificationResult.Status.FAILED));

    assertThat(cred.getVerificationStatus().error().get(),
        equalTo(VERIFICATION_ERROR));
    
    assertThat(cred.getCertificateChain(), emptyArray());
  }

}
