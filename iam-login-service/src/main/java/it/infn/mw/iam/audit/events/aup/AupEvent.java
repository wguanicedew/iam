package it.infn.mw.iam.audit.events.aup;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
import it.infn.mw.iam.persistence.model.IamAup;

public abstract class AupEvent extends IamAuditApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private final IamAup aup;


  protected AupEvent(Object source, String message, IamAup aup) {
    super(IamEventCategory.AUP, source, message);
    this.aup = aup;
  }

  public IamAup getAup() {
    return aup;
  }
}
