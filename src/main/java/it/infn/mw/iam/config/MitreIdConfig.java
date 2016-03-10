package it.infn.mw.iam.config;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.sql.DataSource;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.encryption.service.impl.DefaultJWTEncryptionAndDecryptionService;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.mitre.oauth2.token.StructuredScopeAwareOAuth2RequestValidator;
import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.mitre.openid.connect.config.JsonMessageSource;
import org.mitre.openid.connect.filter.MultiUrlRequestMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.nimbusds.jose.JWEAlgorithm;

@Configuration
public class MitreIdConfig {

  @Autowired
  DataSource dataSource;
  
  @Bean
  public OAuth2RequestValidator oauthRequestValidator() {

    return new StructuredScopeAwareOAuth2RequestValidator();
  }

  @Bean
  public PlatformTransactionManager defaultTransactionManager() {

    return new DataSourceTransactionManager(dataSource);
  }
  
  
  @Bean
  public ConfigurationPropertiesBean config() {

    ConfigurationPropertiesBean config = new ConfigurationPropertiesBean();
    config.setIssuer("http://localhost:8080");
    config.setLogoImageUrl(null);
    config.setTopbarTitle("IAM server");
    config.setRegTokenLifeTime(172800L);
    config.setForceHttps(false);
    config.setLocale(Locale.ENGLISH);

    return config;
  }

  @Bean
  public JWKSetKeyStore defaultKeyStore() {

    Resource location = new FileSystemResource(
      "src/main/resources/keystore.jwks");

    JWKSetKeyStore keyStore = new JWKSetKeyStore();
    keyStore.setLocation(location);

    return keyStore;
  }

  @Bean
  public DefaultJWTEncryptionAndDecryptionService defaultEncryptionService()
    throws Exception {

    DefaultJWTEncryptionAndDecryptionService encryptionService = null;
    encryptionService = new DefaultJWTEncryptionAndDecryptionService(
      defaultKeyStore());
    encryptionService.setDefaultAlgorithm(JWEAlgorithm.RSA1_5);
    encryptionService.setDefaultDecryptionKeyId("rsa1");
    encryptionService.setDefaultEncryptionKeyId("rsa1");

    return encryptionService;
  }

  @Bean
  public DefaultJWTSigningAndValidationService defaultSignerService()
    throws Exception {

    DefaultJWTSigningAndValidationService signerService = null;

    signerService = new DefaultJWTSigningAndValidationService(
      defaultKeyStore());
    signerService.setDefaultSignerKeyId("rsa1");
    signerService.setDefaultSigningAlgorithmName("RS256");

    return signerService;
  }

  @Bean
  public Http403ForbiddenEntryPoint http403EntryPoint() {

    return new Http403ForbiddenEntryPoint();
  }

  @Bean
  public WebResponseExceptionTranslator oauth2ExceptionTranslator() {

    return new DefaultWebResponseExceptionTranslator();
  }
  
  @Bean
  public LocaleResolver localeResolver(){
    SessionLocaleResolver slr = new SessionLocaleResolver();
    slr.setDefaultLocale(Locale.US);
    return slr;
  }

  @Bean
  public MessageSource messageSource() {

    DefaultResourceLoader loader = new DefaultResourceLoader();
    JsonMessageSource messageSource = new JsonMessageSource();
    messageSource
      .setBaseDirectory(loader.getResource("resources/js/locale/"));
    messageSource.setUseCodeAsDefaultMessage(true);

    return messageSource;
  }

  @Bean
  public RequestMatcher clientAuthMatcher() {

    Set<String> endpoints = new LinkedHashSet<String>(
      Arrays.asList("/introspect", "/revoke", "/token"));
    return new MultiUrlRequestMatcher(endpoints);
  }
}
