package it.infn.mw.iam.audit.events.group.request;

import it.infn.mw.iam.persistence.model.IamGroupRequest;

public class GroupRequestApprovedEvent extends GroupRequestEvent {

  private static final long serialVersionUID = 1L;

  public GroupRequestApprovedEvent(Object source, IamGroupRequest groupRequest) {
    super(source, groupRequest, "Group membership request approved");
  }
}
