package it.infn.mw.iam.audit.events.scope_policy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
import it.infn.mw.iam.audit.utils.IamScopePolicySerializer;
import it.infn.mw.iam.persistence.model.IamScopePolicy;

public abstract class ScopePolicyEvent extends IamAuditApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @JsonSerialize(using=IamScopePolicySerializer.class)
  private final IamScopePolicy policy;
  
  protected ScopePolicyEvent(Object source, String message, IamScopePolicy policy) {
    super(IamEventCategory.SCOPE_POLICY, source, message);
    this.policy = policy;
  }

  public IamScopePolicy getPolicy() {
    return policy;
  }

}
