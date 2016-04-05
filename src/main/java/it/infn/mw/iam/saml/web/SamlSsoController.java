package it.infn.mw.iam.saml.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/saml")
public class SamlSsoController {

  public static final Logger LOG = LoggerFactory
    .getLogger(SamlSsoController.class);

  @Autowired
  MetadataManager metadata;

  @RequestMapping(value = "/idpSelection", method = RequestMethod.GET)
  public String idpSelection(HttpServletRequest request, Model model) {

    if (!(SecurityContextHolder.getContext()
      .getAuthentication() instanceof AnonymousAuthenticationToken)) {
      LOG.warn("The current user is already logged.");
      return "redirect:/";
    } else {

      Set<String> idps = metadata.getIDPEntityNames();
      for (String idp : idps) {
        LOG.info("Configured Identity Provider for SSO: " + idp);
      }
      model.addAttribute("idps", idps);
      return "samlIdpSelection";
    }
  }
}
