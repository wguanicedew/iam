package it.infn.mw.iam.test.ext_authn.x509;

import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.security.cert.X509Certificate;

import org.junit.Test;

import it.infn.mw.iam.authn.x509.CanlX509CertificateChainParser;
import it.infn.mw.iam.authn.x509.X509CertificateChainParser;

public class X509CertificateParserTests extends X509TestSupport{

  
  @Test
  public void testCertificateParsing() {
    X509CertificateChainParser parser = new CanlX509CertificateChainParser();
    X509Certificate[] chain = parser.parseChainFromString(TEST_0_CERT_STRING);
    
    assertThat(chain, arrayWithSize(1));
    assertThat(chain[0].getSubjectX500Principal().getName(), equalTo(TEST_0_SUBJECT));
    
  }
  
  @Test(expected=RuntimeException.class)
  public void testCertificateParsingFailsWithGarbage(){
    X509CertificateChainParser parser = new CanlX509CertificateChainParser();
    try{
      parser.parseChainFromString("48327498dsahtdsadasgyr9");
    }catch(RuntimeException e){
      assertThat(e.getMessage(), containsString("PEM data not found"));
      throw e;
    }
  }
  
  @Test(expected=RuntimeException.class)
  public void testCertificateParsingFailsWithEmptyString(){
    X509CertificateChainParser parser = new CanlX509CertificateChainParser();
    try{
      parser.parseChainFromString("");
    }catch(RuntimeException e){
      assertThat(e.getMessage(), containsString("PEM data not found"));
      throw e;
    }
  }
}
