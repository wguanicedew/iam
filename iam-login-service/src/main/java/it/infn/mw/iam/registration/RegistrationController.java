package it.infn.mw.iam.registration;

import static it.infn.mw.iam.api.scim.controller.utils.ValidationHelper.handleValidationError;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Sets;

import it.infn.mw.iam.api.scim.exception.ResourceNotFoundException;
import it.infn.mw.iam.api.scim.model.ScimConstants;
import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.api.scim.provisioning.ScimUserProvisioning;
import it.infn.mw.iam.core.IamRegistrationRequestStatus;
import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;
import it.infn.mw.iam.persistence.repository.IamAccountRepository;
import it.infn.mw.iam.persistence.repository.IamRegistrationRequestRepository;

@RestController
@Transactional
public class RegistrationController {

  @Autowired
  private IamAccountRepository accountRepository;

  @Autowired
  private IamRegistrationRequestRepository requestRepository;

  @Autowired
  private ScimUserProvisioning userService;

  @Autowired
  private RegistrationConverter converter;

  @Autowired
  private TokenGenerator tokenGenerator;

  @PreAuthorize("#oauth2.hasScope('registration:list') or hasRole('ADMIN')")
  @RequestMapping(value = "/registration", method = RequestMethod.GET)
  @ResponseBody
  public List<RegistrationRequestDto> listRequests(
    @RequestParam("status") final IamRegistrationRequestStatus status) {

    List<IamRegistrationRequest> result = requestRepository.findByStatus(status)
      .get();

    List<RegistrationRequestDto> requests = new ArrayList<>();
    for (IamRegistrationRequest elem : result) {
      RegistrationRequestDto item = converter.fromEntity(elem);
      requests.add(item);
    }

    return requests;
  }

  @RequestMapping(value = "/registration", method = RequestMethod.POST,
    consumes = ScimConstants.SCIM_CONTENT_TYPE)
  public RegistrationRequestDto createRegistrationRequest(
    @RequestBody @Validated(ScimUser.NewUserValidation.class) final ScimUser user,
    final BindingResult validationResult) {

    handleValidationError("Invalid user", validationResult);

    IamAccount newAccount = userService.createAccount(user);
    newAccount.setConfirmationKey(tokenGenerator.generateToken());
    newAccount.setActive(false);

    IamRegistrationRequest request = new IamRegistrationRequest();
    request.setUuid(UUID.randomUUID()
      .toString());
    request.setCreationTime(new Date());
    request.setAccount(newAccount);
    request.setStatus(IamRegistrationRequestStatus.NEW);

    requestRepository.save(request);

    return converter.fromEntity(request);
  }

  @PreAuthorize("#oauth2.hasScope('registration:update') or hasRole('ADMIN')")
  @RequestMapping(value = "/registration/{uuid}", method = RequestMethod.POST)
  public RegistrationRequestDto changeStatus(
    @PathVariable("uuid") final String uuid,
    @RequestParam("decision") final String decision) {

    IamRegistrationRequestStatus newStatus = IamRegistrationRequestStatus
      .valueOf(decision);

    IamRegistrationRequest reg = requestRepository.findByUuid(uuid)
      .orElseThrow(() -> new ResourceNotFoundException(
        "No request mapped to uuid '" + uuid + "'"));

    if (Sets.newHashSet(IamRegistrationRequestStatus.APPROVED,
      IamRegistrationRequestStatus.REJECTED)
      .contains(newStatus)
      && !reg.getStatus()
        .equals(IamRegistrationRequestStatus.CONFIRMED)) {
      throw new IllegalArgumentException(String.format(
        "Bad status transition from [%s] to [%s]", reg.getStatus(), newStatus));
    }

    reg.setStatus(newStatus);
    reg.setLastUpdateTime(new Date());
    requestRepository.save(reg);

    if (IamRegistrationRequestStatus.APPROVED.equals(newStatus)) {
      IamAccount account = reg.getAccount();
      account.setActive(true);
      account.setLastUpdateTime(new Date());

      accountRepository.save(account);
    }

    return converter.fromEntity(reg);
  }

  @RequestMapping(value = "/registration/confirm/{token}",
    method = RequestMethod.POST)
  public RegistrationRequestDto confirmEmail(
    @PathVariable("token") final String token) {

    IamRegistrationRequest reg = requestRepository
      .findByAccountConfirmationKey(token)
      .orElseThrow(() -> new ResourceNotFoundException(String.format(
        "No registration request found for registration_key [%s]", token)));

    reg.setStatus(IamRegistrationRequestStatus.CONFIRMED);
    reg.setLastUpdateTime(new Date());
    reg.getAccount()
      .getUserInfo()
      .setEmailVerified(true);

    requestRepository.save(reg);

    return converter.fromEntity(reg);
  }

}
