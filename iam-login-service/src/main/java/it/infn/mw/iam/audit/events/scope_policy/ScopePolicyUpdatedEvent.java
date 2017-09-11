package it.infn.mw.iam.audit.events.scope_policy;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public class ScopePolicyUpdatedEvent extends ScopePolicyEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  public ScopePolicyUpdatedEvent(Object source, IamScopePolicy policy) {
    super(source, "Scope policy updated", policy);
  }

}
