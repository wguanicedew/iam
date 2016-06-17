package it.infn.mw.iam.config;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.encryption.service.impl.DefaultJWTEncryptionAndDecryptionService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.nimbusds.jose.JWEAlgorithm;

@Configuration
public class CriptoConfig {

  @Value("${iam.keyStoreLocation}")
  String keyStoreLocation;

  @Autowired
  ResourceLoader resourceLoader;

  @Bean(name = "defaultKeyStore")
  public JWKSetKeyStore defaultKeyStore() {

    Resource keyStoreResource = resourceLoader.getResource(keyStoreLocation);

    JWKSetKeyStore keyStore = new JWKSetKeyStore();
    keyStore.setLocation(keyStoreResource);

    return keyStore;
  }

  @Bean(name = "defaultsignerService")
  public DefaultJWTSigningAndValidationService defaultSignerService() throws Exception {

    DefaultJWTSigningAndValidationService signerService = null;

    signerService = new DefaultJWTSigningAndValidationService(defaultKeyStore());
    signerService.setDefaultSignerKeyId("rsa1");
    signerService.setDefaultSigningAlgorithmName("RS256");

    return signerService;
  }

  @Bean(name = "defaultEncryptionService")
  public DefaultJWTEncryptionAndDecryptionService defaultEncryptionService() throws Exception {

    DefaultJWTEncryptionAndDecryptionService encryptionService = null;
    encryptionService = new DefaultJWTEncryptionAndDecryptionService(defaultKeyStore());
    encryptionService.setDefaultAlgorithm(JWEAlgorithm.RSA1_5);
    encryptionService.setDefaultDecryptionKeyId("rsa1");
    encryptionService.setDefaultEncryptionKeyId("rsa1");

    return encryptionService;
  }

}
