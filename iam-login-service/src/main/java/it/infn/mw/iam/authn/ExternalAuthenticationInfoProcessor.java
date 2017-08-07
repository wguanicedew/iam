package it.infn.mw.iam.authn;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
@FunctionalInterface
public interface ExternalAuthenticationInfoProcessor {

  Map<String, String> process(OAuth2Authentication authentication);

}
