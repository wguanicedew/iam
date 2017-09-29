package it.infn.mw.iam.audit.events.scope_policy;

import it.infn.mw.iam.persistence.model.IamScopePolicy;

public class ScopePolicyDeletedEvent extends ScopePolicyEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
 
  public ScopePolicyDeletedEvent(Object source, IamScopePolicy policy) {
    super(source, "Scope policy deleted", policy);
  }

}
