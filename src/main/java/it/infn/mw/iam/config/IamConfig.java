package it.infn.mw.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

import it.infn.mw.iam.core.IamUserDetailsService;

@Configuration
public class IamConfig {

  @Bean(name="iamUserDetailsService")
  UserDetailsService iamUserDetailsService(){
    return new IamUserDetailsService();
  }

  
}
