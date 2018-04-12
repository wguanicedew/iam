package it.infn.mw.iam.audit.events.group.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
import it.infn.mw.iam.audit.utils.IamGroupRequestSerializer;
import it.infn.mw.iam.persistence.model.IamGroupRequest;

public abstract class GroupRequestEvent extends IamAuditApplicationEvent {

  private static final long serialVersionUID = 1L;

  @JsonSerialize(using = IamGroupRequestSerializer.class)
  private final IamGroupRequest groupRequest;

  public GroupRequestEvent(Object source, IamGroupRequest groupRequest, String message) {
    super(IamEventCategory.MEMBERSHIP, source, message);
    this.groupRequest = groupRequest;
  }

  public IamGroupRequest getGroupRequest() {
    return groupRequest;
  }
}
