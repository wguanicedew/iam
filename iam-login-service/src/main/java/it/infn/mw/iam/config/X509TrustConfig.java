package it.infn.mw.iam.config;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.SocketFactoryCreator;

@Configuration
@Profile("canl")
public class X509TrustConfig {

  @Value("${x509.trustAnchorsDir}")
  String trustAnchorsDir;

  @Value("${x509.trustAnchorsRefreshMsec}")
  Long trustAnchorsRefreshInterval;

  @Bean
  public X509CertChainValidatorExt certificateValidator() {

    return new CertificateValidatorBuilder().lazyAnchorsLoading(false)
      .trustAnchorsDir(trustAnchorsDir)
      .trustAnchorsUpdateInterval(trustAnchorsRefreshInterval.longValue())
      .build();
  }

  @Bean
  public SSLContext sslContext() {

    try {
      SSLContext context = SSLContext.getInstance("TLSv1");

      X509TrustManager tm = SocketFactoryCreator
        .getSSLTrustManager(certificateValidator());
      SecureRandom r = new SecureRandom();
      context.init(null, new TrustManager[] { tm }, r);

      return context;

    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException(e);
    }

  }

  @Bean
  public HttpClient httpClient() {

    SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(
      sslContext());

    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
      .<ConnectionSocketFactory> create()
      .register("https", sf)
      .register("http", PlainConnectionSocketFactory.getSocketFactory())
      .build();

    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
      socketFactoryRegistry);
    connectionManager.setMaxTotal(10);
    connectionManager.setDefaultMaxPerRoute(10);

    return HttpClientBuilder.create()
      .setConnectionManager(connectionManager)
      .disableAuthCaching()
      .build();
  }

  @Bean(name = "canlRequestFactory")
  public ClientHttpRequestFactory httpRequestFactory() {

    return new HttpComponentsClientHttpRequestFactory(httpClient());
  }

}
