package it.infn.mw.iam.test.actuator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import it.infn.mw.iam.test.util.MockMailHealthIndicator;

@Configuration
public class MailHealthEndpointsTestsConfig {

  
  @Bean 
  @Primary
  MockMailHealthIndicator healthIndicator() {
    return new MockMailHealthIndicator();
  }

}
