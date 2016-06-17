package it.infn.mw.iam.api.scim.controller;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import it.infn.mw.iam.api.scim.controller.utils.ValidationErrorMessageHelper;
import it.infn.mw.iam.api.scim.exception.ScimValidationException;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimListResponse;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.model.ScimUserPatchRequest;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.api.scim.provisioning.paging.DefaultScimPageRequest;
import it.infn.mw.iam.api.scim.provisioning.paging.ScimPageRequest;

@RestController
@RequestMapping("/scim/Users")
@Transactional
public class UserController {

  private static final int SCIM_MAX_PAGE_SIZE = 100;

  @Autowired
  ScimUserProvisioning userProvisioningService;

  private Set<String> parseAttributes(String attributesParameter) {

    Set<String> result = new HashSet<>();
    if (!Strings.isNullOrEmpty(attributesParameter)) {
      result = Sets.newHashSet(Splitter.on(CharMatcher.anyOf(".,")).trimResults().omitEmptyStrings()
          .split(attributesParameter));
    }
    result.add("schemas");
    result.add("id");
    return result;
  }

  private void handleValidationError(String message, BindingResult validationResult) {

    if (validationResult.hasErrors()) {
      throw new ScimValidationException(
          ValidationErrorMessageHelper.buildValidationErrorMessage(message, validationResult));
    }
  }

  private ScimPageRequest buildPageRequest(Integer count, Integer startIndex) {

    if (count == null || count > SCIM_MAX_PAGE_SIZE) {
      count = SCIM_MAX_PAGE_SIZE;
    }

    if (count < 0) {
      count = 0;
    }

    // SCIM pages index is 1-based
    if (startIndex == null || startIndex < 1) {
      startIndex = 1;
    }

    ScimPageRequest pr =
        new DefaultScimPageRequest.Builder().count(count).startIndex(startIndex - 1).build();

    return pr;
  }

  @PreAuthorize("#oauth2.hasScope('scim:read') or hasRole('ADMIN')")
  @RequestMapping(method = RequestMethod.GET, produces = ScimConstants.SCIM_CONTENT_TYPE)
  public MappingJacksonValue listUsers(@RequestParam(required = false) Integer count,
      @RequestParam(required = false) Integer startIndex,
      @RequestParam(required = false) String attributes) {

    ScimPageRequest pr = buildPageRequest(count, startIndex);
    ScimListResponse<ScimUser> result = userProvisioningService.list(pr);

    MappingJacksonValue wrapper = new MappingJacksonValue(result);

    if (attributes != null) {
      Set<String> includeAttributes = parseAttributes(attributes);

      FilterProvider filterProvider = new SimpleFilterProvider().addFilter("attributeFilter",
          SimpleBeanPropertyFilter.filterOutAllExcept(includeAttributes));

      wrapper.setFilters(filterProvider);
    }

    return wrapper;
  }

  @PreAuthorize("#oauth2.hasScope('scim:read') or hasRole('ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.GET,
      produces = ScimConstants.SCIM_CONTENT_TYPE)
  public ScimUser getUser(@PathVariable final String id) {

    return userProvisioningService.getById(id);
  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('ADMIN')")
  @RequestMapping(method = RequestMethod.POST, consumes = ScimConstants.SCIM_CONTENT_TYPE,
      produces = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.CREATED)
  public ScimUser create(@RequestBody @Validated(ScimUser.NewUserValidation.class) ScimUser user,
      BindingResult validationResult) {

    handleValidationError("Invalid Scim User", validationResult);
    ScimUser result = userProvisioningService.create(user);
    return result;
  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT,
      consumes = ScimConstants.SCIM_CONTENT_TYPE, produces = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.OK)
  public ScimUser replaceUser(@PathVariable final String id,
      @RequestBody @Validated(ScimUser.NewUserValidation.class) ScimUser user,
      BindingResult validationResult) {

    handleValidationError("Invalid Scim User", validationResult);

    return userProvisioningService.replace(id, user);

  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.PATCH,
      consumes = ScimConstants.SCIM_CONTENT_TYPE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateUser(@PathVariable final String id,
      @RequestBody ScimUserPatchRequest patchRequest) {

    userProvisioningService.update(id, patchRequest.getOperations());

  }

  @PreAuthorize("#oauth2.hasScope('scim:write') or hasRole('ADMIN')")
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUser(@PathVariable final String id) {

    userProvisioningService.delete(id);
  }

}
