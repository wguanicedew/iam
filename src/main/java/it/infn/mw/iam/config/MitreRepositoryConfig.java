package it.infn.mw.iam.config;

import org.mitre.oauth2.repository.OAuth2ClientRepository;
import org.mitre.oauth2.repository.OAuth2TokenRepository;
import org.mitre.oauth2.repository.SystemScopeRepository;
import org.mitre.oauth2.repository.impl.JpaOAuth2ClientRepository;
import org.mitre.oauth2.repository.impl.JpaOAuth2TokenRepository;
import org.mitre.oauth2.repository.impl.JpaSystemScopeRepository;
import org.mitre.openid.connect.repository.ApprovedSiteRepository;
import org.mitre.openid.connect.repository.BlacklistedSiteRepository;
import org.mitre.openid.connect.repository.PairwiseIdentifierRepository;
import org.mitre.openid.connect.repository.UserInfoRepository;
import org.mitre.openid.connect.repository.WhitelistedSiteRepository;
import org.mitre.openid.connect.repository.impl.JpaApprovedSiteRepository;
import org.mitre.openid.connect.repository.impl.JpaBlacklistedSiteRepository;
import org.mitre.openid.connect.repository.impl.JpaPairwiseIdentifierRepository;
import org.mitre.openid.connect.repository.impl.JpaUserInfoRepository;
import org.mitre.openid.connect.repository.impl.JpaWhitelistedSiteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MitreRepositoryConfig {

  @Bean
  PairwiseIdentifierRepository defaultPairwiseIdentifierRepository() {

    return new JpaPairwiseIdentifierRepository();
  }

  @Bean
  UserInfoRepository defaultUserInfoRepository() {

    return new JpaUserInfoRepository();
  }

  @Bean
  OAuth2ClientRepository defaultOAuth2ClientRepository() {

    return new JpaOAuth2ClientRepository();
  }

  @Bean
  OAuth2TokenRepository defaultOAuth2TokenRepository() {

    return new JpaOAuth2TokenRepository();
  }

  @Bean
  ApprovedSiteRepository defaultApprovedSiteRepository() {

    return new JpaApprovedSiteRepository();
  }

  @Bean
  WhitelistedSiteRepository defaultWhitelistedSiteRepository() {

    return new JpaWhitelistedSiteRepository();
  }

  @Bean
  BlacklistedSiteRepository defaultBlacklistedSiteRepository() {

    return new JpaBlacklistedSiteRepository();
  }

  @Bean
  SystemScopeRepository defaultSystemScopeRepository() {

    return new JpaSystemScopeRepository();
  }

}
