package it.infn.mw.iam.api.scim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RestController
@RequestMapping("/scim/Me")
public class MeController {

  private final IamAccountRepository iamAccountRepository;
  private final UserConverter userConverter;

  @Autowired
  public MeController(IamAccountRepository iamAccountRepository, UserConverter userConverter) {

    this.iamAccountRepository = iamAccountRepository;
    this.userConverter = userConverter;
  }

  @PreAuthorize("#oauth2.hasScope('scim:read') or hasRole('USER')")
  @RequestMapping(method = RequestMethod.GET)
  public ScimUser whoami() {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (!(auth.getPrincipal() instanceof User)) {

      throw new ScimException("Authenticated User details not found!");
    }

    User user = (User) auth.getPrincipal();

    IamAccount account = iamAccountRepository.findByUsername(user.getUsername())
      .orElseThrow(() -> new ScimResourceNotFoundException(
          "No user mapped to username '" + user.getUsername() + "'"));

    return userConverter.toScim(account);

  }

}
