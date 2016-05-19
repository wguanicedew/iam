package it.infn.mw.iam.api.scim.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.scim.controller.utils.ValidationErrorMessageHelper;
import it.infn.mw.iam.api.scim.exception.ScimValidationException;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;

@RestController
@RequestMapping("/scim/Users")
@Transactional
public class UserController {

  @Autowired
  ScimUserProvisioning userProvisioningService;

  private void handleValidationError(String message,
    BindingResult validationResult) {

    if (validationResult.hasErrors()) {
      throw new ScimValidationException(ValidationErrorMessageHelper
        .buildValidationErrorMessage(message, validationResult));
    }
  }

  @PreAuthorize("(#oauth2.hasScope('scim:read') or #oauth2.hasScope('scim:write')) or hasRole('ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.GET,
    produces = ScimConstants.SCIM_CONTENT_TYPE)
  public ScimUser getUser(@PathVariable final String id) {

    return userProvisioningService.getById(id);
  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('ADMIN')")
  @RequestMapping(method = RequestMethod.POST,
    consumes = ScimConstants.SCIM_CONTENT_TYPE,
    produces = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.CREATED)
  public ScimUser create(
    @RequestBody @Validated(ScimUser.NewUserValidation.class) ScimUser user,
    BindingResult validationResult) {

    handleValidationError("Invalid Scim User", validationResult);
    ScimUser result = userProvisioningService.create(user);
    return result;
  }

}
