package it.infn.mw.iam.authn.saml.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.metadata.MetadataManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.infn.mw.iam.authn.saml.MetadataLookupService;
import it.infn.mw.iam.authn.saml.model.IdpDescription;

@Controller
@RequestMapping("/saml")
@Profile("saml")
public class SamlSsoController {

  public static final Logger LOG = LoggerFactory.getLogger(SamlSsoController.class);

  @Autowired
  MetadataManager metadata;

  @Autowired
  MetadataLookupService lookupService;

  @RequestMapping(value = "/idpSelection", method = RequestMethod.GET)
  public String idpSelection(final HttpServletRequest request, final Model model) {

    if (!(SecurityContextHolder.getContext()
      .getAuthentication() instanceof AnonymousAuthenticationToken)) {
      LOG.warn("The current user is already logged.");
      return "redirect:/";
    } else {

      model.addAttribute("idps", lookupService.listIdps());
      return "idpSelection";
    }
  }

  @RequestMapping(value = "/idps", method = RequestMethod.GET)
  public @ResponseBody List<IdpDescription> idps(
      @RequestParam(value = "q", required = false) String text) {

    if (text == null) {
      return lookupService.listIdps();
    }

    return lookupService.lookupIdp(text);
  }
}
