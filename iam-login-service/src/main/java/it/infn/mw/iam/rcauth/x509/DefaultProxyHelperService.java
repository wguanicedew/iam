package it.infn.mw.iam.rcauth.x509;

import static org.italiangrid.voms.util.CredentialsUtils.saveProxyCredentials;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;
import eu.emi.security.authn.x509.proxy.ProxyType;

@Service
public class DefaultProxyHelperService implements ProxyHelperService {
  public static final int DEFAULT_KEY_SIZE = 2048;

  final Clock clock;

  @Autowired
  public DefaultProxyHelperService(Clock clock) {
    this.clock = clock;
  }

  @Override
  public ProxyCertificate generateProxy(X509Certificate cert, PrivateKey key) {

    ProxyCertificateOptions options = new ProxyCertificateOptions(new X509Certificate[] {cert});
    options.setKeyLength(DEFAULT_KEY_SIZE);
    options.setType(ProxyType.RFC3820);

    Instant now = clock.instant();
    Instant certNotAfter = cert.getNotAfter().toInstant();

    long lifeTimeInSeconds = Duration.between(now, certNotAfter).get(ChronoUnit.SECONDS);
    options.setLifetime(lifeTimeInSeconds, TimeUnit.SECONDS);

    try {
      return ProxyGenerator.generate(options, key);
    } catch (InvalidKeyException | CertificateParsingException | SignatureException
        | NoSuchAlgorithmException | IOException e) {
      throw new ProxyGenerationError(e);
    }

  }

  @Override
  public String proxyCertificateToPemString(ProxyCertificate proxy) {
    ByteArrayOutputStream proxyOs = new ByteArrayOutputStream();
    try {
      saveProxyCredentials(proxyOs, proxy.getCredential());
      proxyOs.flush();
      return proxyOs.toString();
    } catch (IllegalStateException | IOException e) {
      throw new ProxyGenerationError("Error serializing proxy certificate: " + e.getMessage(), e);
    }
  }

}
