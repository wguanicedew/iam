package it.infn.mw.iam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilder jacksonBuilder() {

    Jackson2ObjectMapperBuilder jacksonBuilder = new Jackson2ObjectMapperBuilder();
    jacksonBuilder.filters(new SimpleFilterProvider().setFailOnUnknownId(false));
    return jacksonBuilder;

  }

}
