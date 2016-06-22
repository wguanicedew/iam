package it.infn.mw.iam.registration;

import java.util.List;

import it.infn.mw.iam.api.scim.model.ScimUser;
import it.infn.mw.iam.core.IamRegistrationRequestStatus;

public interface RegistrationRequestService {

  public RegistrationRequestDto create(ScimUser user);

  public List<RegistrationRequestDto> list(IamRegistrationRequestStatus status);

  public RegistrationRequestDto updateStatus(String uuid, IamRegistrationRequestStatus status);

  public RegistrationRequestDto confirmRequest(String confirmationKey);

}
