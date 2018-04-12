package it.infn.mw.iam.notification;

import it.infn.mw.iam.persistence.model.IamAccount;
import it.infn.mw.iam.persistence.model.IamEmailNotification;
import it.infn.mw.iam.persistence.model.IamGroupRequest;
import it.infn.mw.iam.persistence.model.IamRegistrationRequest;

public interface NotificationFactory {

  IamEmailNotification createConfirmationMessage(IamRegistrationRequest request);

  IamEmailNotification createAccountActivatedMessage(IamRegistrationRequest request);

  IamEmailNotification createRequestRejectedMessage(IamRegistrationRequest request);

  IamEmailNotification createAdminHandleRequestMessage(IamRegistrationRequest request);

  IamEmailNotification createResetPasswordMessage(IamAccount account);

  IamEmailNotification createAdminHandleGroupRequestMessage(IamGroupRequest groupRequest);

  IamEmailNotification createGroupMembershipApprovedMessage(IamGroupRequest groupRequest);

  IamEmailNotification createGroupMembershipRejectedMessage(IamGroupRequest groupRequest);
}
