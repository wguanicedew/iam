package it.infn.mw.iam.audit.events.group.request;

import it.infn.mw.iam.persistence.model.IamGroupRequest;

public class GroupRequestRejectedEvent extends GroupRequestEvent {

  private static final long serialVersionUID = 1L;

  public GroupRequestRejectedEvent(Object source, IamGroupRequest groupRequest) {
    super(source, groupRequest, "Group membership request rejected");
  }
}
