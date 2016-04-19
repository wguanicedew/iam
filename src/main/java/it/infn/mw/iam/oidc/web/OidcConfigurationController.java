package it.infn.mw.iam.oidc.web;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.infn.mw.iam.oidc.AccountChooserConfigurationProvider;
import it.infn.mw.iam.oidc.OidcProviderTO;

@Controller
@RequestMapping("/oidc")
@Profile("oidc")
public class OidcConfigurationController {

  @Autowired
  AccountChooserConfigurationProvider conf;

  @RequestMapping(value = "/clients", method = RequestMethod.GET,
    produces = "application/json")
  public @ResponseBody List<String> clients() {

    return conf.clientRedirectURIs();

  }

  @RequestMapping(value = "/providers", method = RequestMethod.GET,
    produces = "application/json")
  public @ResponseBody Collection<OidcProviderTO> providers() {

    return conf.providers();
  }

}
