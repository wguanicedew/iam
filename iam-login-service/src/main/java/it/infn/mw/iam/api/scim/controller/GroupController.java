package it.infn.mw.iam.api.scim.controller;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.infn.mw.iam.api.scim.exception.ScimValidationException;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimGroup;
import it.infn.mw.iam.api.scim.provisioning.ScimGroupProvisioning;

@RestController
@RequestMapping("/scim/Groups")
@Transactional
public class GroupController {

  @Autowired
  ScimGroupProvisioning groupProvisioningService;
  
  private String buildValidationErrorMessage(String errorMessage,
    BindingResult validationResult) {

    StringBuilder validationError = new StringBuilder();
    validationError.append(errorMessage + ": ");

    boolean first = true;

    for (ObjectError error : validationResult.getAllErrors()) {

      if (!first) {
        validationError.append(",");
      }

      if (error instanceof FieldError) {
        FieldError fieldError = (FieldError) error;

        validationError
          .append(String.format("[%s.%s : %s]", fieldError.getObjectName(),
            fieldError.getField(), fieldError.getDefaultMessage()));

      } else {

        validationError.append(String.format("[%s : %s]", error.getObjectName(),
          error.getDefaultMessage()));
      }

      first = false;
    }

    return validationError.toString();
  }
  
  private void handleValidationError(String message,
    BindingResult validationResult) {

    if (validationResult.hasErrors()) {
      throw new ScimValidationException(
        buildValidationErrorMessage(message, validationResult));
    }
  }
  
  @RequestMapping(value = "/{id}", method = RequestMethod.GET,
    produces = ScimConstants.SCIM_CONTENT_TYPE)
  public ScimGroup getGroup(@PathVariable final String id) {

    return groupProvisioningService.getById(id);
  }
  
  @RequestMapping(method = RequestMethod.POST,
	consumes = ScimConstants.SCIM_CONTENT_TYPE,
	produces = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.CREATED)
  public ScimGroup create(@RequestBody ScimGroup group,
	BindingResult validationResult) {

	handleValidationError("Invalid Scim Group", validationResult);
	ScimGroup result = groupProvisioningService.create(group);
	return result;
  }
}
