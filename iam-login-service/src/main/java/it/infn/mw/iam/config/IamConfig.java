/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package it.infn.mw.iam.config;

import org.h2.server.web.WebServlet;
import org.mitre.oauth2.service.IntrospectionResultAssembler;
import org.mitre.oauth2.service.impl.DefaultOAuth2AuthorizationCodeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import it.infn.mw.iam.api.account.AccountUtils;
import it.infn.mw.iam.api.aup.AUPSignatureCheckService;
import it.infn.mw.iam.authn.oidc.OidcTokenEnhancer;
import it.infn.mw.iam.core.IamIntrospectionResultAssembler;
import it.infn.mw.iam.core.IamProperties;
import it.infn.mw.iam.core.web.EnforceAupFilter;
import it.infn.mw.iam.persistence.repository.IamAupRepository;
import it.infn.mw.iam.util.DumpHeadersFilter;

@Configuration
public class IamConfig {

  @Value("${iam.organisation.name}")
  private String iamOrganisationName;

  @Bean
  AuthorizationCodeServices authorizationCodeServices() {

    return new DefaultOAuth2AuthorizationCodeService();
  }

  @Bean
  @Primary
  TokenEnhancer iamTokenEnhancer() {

    return new OidcTokenEnhancer();
  }

  @Bean
  IamProperties iamProperties() {

    IamProperties.INSTANCE.setOrganisationName(iamOrganisationName);
    return IamProperties.INSTANCE;
  }

  @Bean
  IntrospectionResultAssembler defaultIntrospectionResultAssembler() {

    return new IamIntrospectionResultAssembler();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  FilterRegistrationBean aupSignatureCheckFilter(AUPSignatureCheckService service,
      AccountUtils utils, IamAupRepository repo) {
    EnforceAupFilter aupFilter = new EnforceAupFilter(service, utils, repo);
    FilterRegistrationBean frb = new FilterRegistrationBean(aupFilter);
    frb.setOrder(Ordered.LOWEST_PRECEDENCE);
    return frb;
  }

  @Bean
  @Profile("dev")
  ServletRegistrationBean h2Console() {

    WebServlet h2Servlet = new WebServlet();
    return new ServletRegistrationBean(h2Servlet, "/h2-console/*");
  }

  @Bean
  @Profile("dev")
  FilterRegistrationBean requestLoggingFilter() {

    DumpHeadersFilter dhf = new DumpHeadersFilter();

    dhf.setIncludeClientInfo(true);
    dhf.setIncludePayload(true);
    dhf.setIncludeQueryString(true);

    FilterRegistrationBean frb = new FilterRegistrationBean(dhf);
    frb.setOrder(0);
    return frb;
  }
}
