package it.infn.mw.iam.core.web.jwk;

import java.util.ArrayList;
import java.util.Map;

import org.mitre.jwt.signer.service.JWTSigningAndValidationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;

@RestController
public class IamJWKSetPublishingEndpoint implements InitializingBean {

  public static final String URL = "jwk";

  private String jsonKeys;

  @Autowired
  private JWTSigningAndValidationService jwtService;

  @RequestMapping(value = "/" + URL, produces = MediaType.APPLICATION_JSON_VALUE)
  public String getJwk() {

    return jsonKeys;
  }

  /**
   * @return the jwtService
   */
  public JWTSigningAndValidationService getJwtService() {
    return jwtService;
  }

  /**
   * @param jwtService the jwtService to set
   */
  public void setJwtService(JWTSigningAndValidationService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public void afterPropertiesSet() throws Exception {

    Map<String, JWK> keys = jwtService.getAllPublicKeys();
    jsonKeys = new JWKSet(new ArrayList<>(keys.values())).toString();
  }

}
