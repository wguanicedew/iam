/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2016-2018
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

import java.util.Locale;
import java.util.Set;

import org.mitre.openid.connect.config.ConfigurationPropertiesBean;
import org.mitre.openid.connect.config.UIConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Sets;

@Configuration
public class MitreConfig {

  @Value("${iam.issuer}")
  private String issuer;

  @Value("${iam.baseUrl}")
  private String baseUrl;

  @Value("${iam.token.lifetime}")
  private Long tokenLifeTime;

  @Value("${iam.logoImageUrl}")
  private String logoImageUrl;

  @Value("${iam.topbarTitle}")
  private String topbarTitle;

  @Bean
  public ConfigurationPropertiesBean config() {

    ConfigurationPropertiesBean config = new ConfigurationPropertiesBean();

    config.setLogoImageUrl(logoImageUrl);
    config.setTopbarTitle(topbarTitle);

    if (!issuer.endsWith("/")) {
      issuer = issuer + "/";
    }

    config.setIssuer(issuer);
    
    if (tokenLifeTime <= 0L){
      config.setRegTokenLifeTime(null);
    } else {
      config.setRegTokenLifeTime(tokenLifeTime);
    }
    
    config.setForceHttps(false);
    config.setLocale(Locale.ENGLISH);

    return config;
  }


  @Bean
  public UIConfiguration uiConfiguration() {
    
    Set<String> jsFiles = Sets.newHashSet("resources/js/client.js",
        "resources/js/grant.js",
        "resources/js/scope.js",
        "resources/js/whitelist.js",
        "resources/js/dynreg.js",
        "resources/js/rsreg.js",
        "resources/js/token.js",
        "resources/js/blacklist.js",
        "resources/js/profile.js");
    
    UIConfiguration config = new UIConfiguration();
    config.setJsFiles(jsFiles);
    return config;

  }
}
