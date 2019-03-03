package it.infn.mw.iam.api.registration.cern;

import static it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH;
import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import it.infn.mw.iam.util.test.saml.SamlSecurityContextBuilder;

@Controller
@Profile({"cern", "mock"})
public class MockCernAuthController {

  @RequestMapping(method = GET, path = "/mock-cern-auth")
  public String mockCernAuthentication(HttpSession session) {

    SamlSecurityContextBuilder builder = new SamlSecurityContextBuilder();
    builder.authorities(EXT_AUTHN_UNREGISTERED_USER_AUTH.getAuthority())
      .subject("123456789")
      .issuer("https://cern.ch/login")
      .email("test@example.org")
      .username("testone")
      .name("Test", "One");

    // CERN registration details
    builder.cernPersonId("123456789")
      .cernFirstName("Test")
      .cernLastName("One")
      .cernEmail("test@example.org")
      .cernHomeInstitute("Test institute");

    SecurityContext samlSecurityContext = builder.buildSecurityContext();
    SecurityContextHolder.clearContext();
    SecurityContextHolder.setContext(samlSecurityContext);
    
    session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, samlSecurityContext);
    
    return "redirect:/cern-registration";
  }
}
