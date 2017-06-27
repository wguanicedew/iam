package it.infn.mw.iam.test.ext_authn.x509;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;

import it.infn.mw.iam.authn.x509.DefaultX509AuthenticationCredentialExtractor;

public class X509TestSupport {

  public static final String TEST_0_CERT_PATH = "src/test/resources/x509/test0.cert.pem";
  public static final String TEST_0_SUBJECT = "CN=test0,O=IGI,C=IT";
  public static final String TEST_0_ISSUER = "CN=Test CA,O=IGI,C=IT";
  public static final String TEST_0_SERIAL = "09";
  public static final String TEST_0_V_START = "Sep 26 15:39:34 2012 GMT";
  public static final String TEST_0_V_END = "Sep 24 15:39:34 2022 GMT";
  
  protected String TEST_0_CERT_STRING;
  
  protected X509TestSupport() {
    try {
      TEST_0_CERT_STRING = new String(Files.readAllBytes(Paths.get(TEST_0_CERT_PATH)));
      // This is how NGINX encodes certficate in the header
      TEST_0_CERT_STRING.replace('\n', '\t');
    } catch (IOException e) {
      throw new AssertionError(e.getMessage(), e);
    }
  }

  protected void mockVerifyHeader(HttpServletRequest request, String content){
    Mockito
    .when(request
      .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.VERIFY.getHeader()))
    .thenReturn(content);
    
  }
  
  
  
  protected HttpHeaders test0SSLHeadersVerificationSuccess(){
    return test0SSLHeaders(true, null); 
  }
  
  protected HttpHeaders test0SSLHeadersVerificationFailed(String verificationError){
    return test0SSLHeaders(false, verificationError); 
  }
  
  private HttpHeaders test0SSLHeaders(boolean verified, String verificationError){
    HttpHeaders headers = new HttpHeaders();
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.CLIENT_CERT.getHeader(), 
        TEST_0_CERT_STRING);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.SUBJECT.getHeader(), 
        TEST_0_SUBJECT);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.ISSUER.getHeader(), 
        TEST_0_ISSUER);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.SERIAL.getHeader(), 
        TEST_0_SERIAL);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.V_START.getHeader(), 
        TEST_0_V_START);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.V_END.getHeader(), 
        TEST_0_V_END);
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.PROTOCOL.getHeader(), 
        "TLS");
    
    headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.SERVER_NAME.getHeader(), 
        "serverName");
    
    if (verified){
      headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.VERIFY.getHeader(), 
        "SUCCESS");
    } else {
      headers.add(DefaultX509AuthenticationCredentialExtractor.Headers.VERIFY.getHeader(), 
          "FAILED:"+verificationError);
    }
    
    return headers;
  }
  protected void mockHttpRequestWithTest0SSLHeaders(HttpServletRequest request) {
    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.CLIENT_CERT.getHeader()))
      .thenReturn(TEST_0_CERT_STRING);

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.SUBJECT.getHeader()))
      .thenReturn(TEST_0_SUBJECT);

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.ISSUER.getHeader()))
      .thenReturn(TEST_0_ISSUER);

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.SERIAL.getHeader()))
      .thenReturn(TEST_0_SERIAL);

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.V_START.getHeader()))
      .thenReturn(TEST_0_V_START);

    Mockito
      .when(
          request.getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.V_END.getHeader()))
      .thenReturn(TEST_0_V_END);

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.PROTOCOL.getHeader()))
      .thenReturn("TLS");

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.SERVER_NAME.getHeader()))
      .thenReturn("serverName");

    Mockito
      .when(request
        .getHeader(DefaultX509AuthenticationCredentialExtractor.Headers.VERIFY.getHeader()))
      .thenReturn("SUCCESS");

  }
}
