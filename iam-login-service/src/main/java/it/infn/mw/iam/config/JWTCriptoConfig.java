/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.infn.mw.iam.config;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.encryption.service.impl.DefaultJWTEncryptionAndDecryptionService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

import it.infn.mw.iam.config.error.IAMJWTKeystoreError;
import it.infn.mw.iam.util.JWKKeystoreLoader;

@Configuration
public class JWTCriptoConfig {
  
  public static final String DEFAULT_JWK_SIGN_ALGO = JWSAlgorithm.RS256.getName();
  public static final JWEAlgorithm DEFAULT_JWE_ENC_ALGO = JWEAlgorithm.RSA_OAEP_256;

  @Autowired
  IamProperties iamProperties;

  @Autowired
  ResourceLoader resourceLoader;

  @Bean
  public JWKKeystoreLoader loader() {
    return new JWKKeystoreLoader(resourceLoader);
  }

  @Bean(name = "defaultKeyStore")
  public JWKSetKeyStore defaultKeyStore(JWKKeystoreLoader loader) {
    return loader.loadKeystoreFromLocation(iamProperties.getJwk().getKeystoreLocation());
  }

  @Bean(name = "defaultsignerService")
  public DefaultJWTSigningAndValidationService defaultSignerService(JWKSetKeyStore keystore) {
    try {
      DefaultJWTSigningAndValidationService signerService =
          new DefaultJWTSigningAndValidationService(keystore);
      signerService.setDefaultSignerKeyId(iamProperties.getJwk().getDefaultKeyId());
      signerService.setDefaultSigningAlgorithmName(DEFAULT_JWK_SIGN_ALGO);
      return signerService;
    } catch (Exception e) {
      throw new IAMJWTKeystoreError("Error creating JWT signing and validation service", e);
    }
  }

  @Bean(name = "defaultEncryptionService")
  public DefaultJWTEncryptionAndDecryptionService defaultEncryptionService(
      JWKSetKeyStore keystore) {

    try {
      DefaultJWTEncryptionAndDecryptionService encryptionService =
          new DefaultJWTEncryptionAndDecryptionService(keystore);
      encryptionService.setDefaultAlgorithm(DEFAULT_JWE_ENC_ALGO);
      encryptionService.setDefaultDecryptionKeyId(iamProperties.getJwk().getDefaultKeyId());
      encryptionService.setDefaultEncryptionKeyId(iamProperties.getJwk().getDefaultKeyId());
      return encryptionService;
    } catch (Exception e) {
      throw new IAMJWTKeystoreError("Error creating JWT encryption/decription service", e);
    }
  }

}
