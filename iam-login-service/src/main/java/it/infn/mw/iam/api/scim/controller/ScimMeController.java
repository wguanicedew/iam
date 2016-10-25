package it.infn.mw.iam.api.scim.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.scim.converter.UserConverter;
import it.infn.mw.iam.api.scim.exception.ScimException;
import it.infn.mw.iam.api.scim.exception.ScimResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;

@RestController
@RequestMapping("/scim/Me")
public class ScimMeController {

  private final IamAccountRepository iamAccountRepository;
  private final UserConverter userConverter;

  @Autowired
  ScimUserProvisioning userProvisioningService;

  @Autowired
  public ScimMeController(IamAccountRepository iamAccountRepository, UserConverter userConverter) {

    this.iamAccountRepository = iamAccountRepository;
    this.userConverter = userConverter;
  }

  @PreAuthorize("#oauth2.hasScope('scim:read') or hasRole('USER')")
  @RequestMapping(method = RequestMethod.GET)
  public ScimUser whoami() {

    IamAccount account = getCurrentUserAccount();
    return userConverter.toScim(account);

  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('USER')")
  @RequestMapping(method = RequestMethod.PATCH, consumes = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(@RequestBody final ScimUserPatchRequest patchRequest) {

    IamAccount account = getCurrentUserAccount();
    userProvisioningService.update(account.getUuid(), patchRequest.getOperations());

  }

  private IamAccount getCurrentUserAccount() throws ScimException, ScimResourceNotFoundException {

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth instanceof OAuth2Authentication) {
      OAuth2Authentication oauth = (OAuth2Authentication) auth;
      if (oauth.getUserAuthentication() == null) {
        throw new ScimException("No user linked to the current OAuth token");
      }
      auth = oauth.getUserAuthentication();
    }

    final String username = auth.getName();

    return iamAccountRepository.findByUsername(username).orElseThrow(
        () -> new ScimResourceNotFoundException("No user mapped to username '" + username + "'"));
  }
}
