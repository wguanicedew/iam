package it.infn.mw.iam.core.web;

import static it.infn.mw.iam.authn.ExternalAuthenticationHandlerSupport.EXT_AUTHN_UNREGISTERED_USER_AUTH;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import it.infn.mw.iam.config.saml.IamSamlProperties;
import it.infn.mw.iam.core.IamProperties;

@Controller
public class LoginController {

  public static final String LOGIN_PAGE_CONFIGURATION_KEY = "loginPageConfiguration";
  public static final String IAM_PROPERTIES_KEY = "iamProperties";

  @Autowired
  LoginPageConfiguration loginPageConfiguration;

  @Autowired
  IamProperties properties;
  
  @Autowired
  IamSamlProperties samlProperties;

  @RequestMapping("/login")
  public String login(Authentication authentication, Model model, HttpServletRequest request) {

    if (authentication == null) {
      return "iam/login";
    }

    if (authentication.getAuthorities().contains(EXT_AUTHN_UNREGISTERED_USER_AUTH)) {
      return "iam/register";
    }

    return "redirect:/";

  }

  @RequestMapping("/register")
  public String register() {
    return "iam/register";
  }

  @RequestMapping("/reset-session")
  public String resetSession(HttpSession session) {

    session.invalidate();

    return "redirect:/";
  }

}
