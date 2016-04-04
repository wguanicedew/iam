package it.infn.mw.iam.config;

import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetailsService;

import it.infn.mw.iam.core.IamProperties;
import it.infn.mw.iam.core.IamUserDetailsService;

@Configuration
public class IamConfig {

  @Value("${iam.organisation.name}")
  private String iamOrganisationName;
  
  @Bean(name="iamUserDetailsService")
  UserDetailsService iamUserDetailsService(){
    return new IamUserDetailsService();
  }
  
  @Bean
  IamProperties iamProperties(){
    IamProperties.INSTANCE.setOrganisationName(iamOrganisationName);
    return IamProperties.INSTANCE;
  }
  
  @Bean
  @Profile("dev")
  ServletRegistrationBean h2Console(){
    WebServlet h2Servlet = new WebServlet();
    return new ServletRegistrationBean(h2Servlet, "/h2-console/*");
  }

  
}
