package it.infn.mw.iam.api.scim.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;

@RestController
@RequestMapping("/scim/Users")
@Transactional
public class UserController {

  @Autowired
  ScimUserProvisioning userProvisioningService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ScimUser getUser(@PathVariable final String id) {

    return userProvisioningService.getById(id);
  }
  
  @RequestMapping(method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.CREATED)
  public ScimUser create(@RequestBody ScimUser user){ 
    return null;
  }

}
