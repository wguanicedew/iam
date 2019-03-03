package it.infn.mw.iam.core.web;

import static java.util.Objects.isNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class DefaultStartRegistrationController {

  public static final String REGISTRATION_PROFILE = "registration";
  public static final String CERN_PROFILE = "cern";

  private boolean registrationProfileEnabled;
  private boolean cernProfileEnabled;

  @Autowired
  public DefaultStartRegistrationController(Environment env) {
    registrationProfileEnabled = cernProfileEnabled = false;

    for (String ap : env.getActiveProfiles()) {
      if (REGISTRATION_PROFILE.equals(ap)) {
        registrationProfileEnabled = true;
      }

      if (CERN_PROFILE.equals(ap)) {
        cernProfileEnabled = true;
      }
    }
  }

  @RequestMapping(method = RequestMethod.GET, path = "/start-registration")
  public String startRegistration(Authentication authentication) {

    if (!isNull(authentication) && authentication.isAuthenticated()) {
      return "iam/dashboard";
    }

    if (registrationProfileEnabled) {
      if (cernProfileEnabled) {
        return "redirect:/cern-registration";
      } else {
        return "iam/register";
      }
    } else {
      return "iam/registrationDisabled";
    }
  }

}
