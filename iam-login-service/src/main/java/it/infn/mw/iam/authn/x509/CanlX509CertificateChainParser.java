package it.infn.mw.iam.authn.x509;

import static eu.emi.security.authn.x509.impl.CertificateUtils.configureSecProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;

@Component
public class CanlX509CertificateChainParser implements X509CertificateChainParser {

  public static final Logger LOG = LoggerFactory.getLogger(CanlX509CertificateChainParser.class);

  public CanlX509CertificateChainParser() {
    configureSecProvider();
  }

  @Override
  public X509Certificate[] parseChainFromString(String certChain) {

    // Replace tabs with newlines (NGINX sends the cert chain PEM over a single line)
    String processedCertChain = certChain.replace('\t', '\n');
    
    InputStream stream = new ByteArrayInputStream(processedCertChain
        .getBytes(StandardCharsets.US_ASCII));

    try {

      X509Certificate[] chain = CertificateUtils.loadCertificateChain(stream, Encoding.PEM);
      return chain;

    } catch (IOException e) {
      final String errorMessage = String.format("Error parsing certificate chain: %s", 
          e.getMessage());
      
      LOG.error(errorMessage, e);
      
      // FIXME: RuntimeException is probably too generic? 
      // Should we have an X509CertficateParsingError instead?
      throw new RuntimeException(errorMessage, e);
    }
  }

}
