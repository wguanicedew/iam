package it.infn.mw.iam.api.scim.controller;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scim/Me")
public class MeController {

  @RequestMapping(method=RequestMethod.GET)
  public void index() {
    throw new NotImplementedException(
      "The /scim/Me endpoint is not implemented");
  }

}
