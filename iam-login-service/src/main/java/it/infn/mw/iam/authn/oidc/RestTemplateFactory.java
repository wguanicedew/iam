package it.infn.mw.iam.authn.oidc;

import org.springframework.web.client.RestTemplate;
@FunctionalInterface
public interface RestTemplateFactory {

  RestTemplate newRestTemplate();

}
