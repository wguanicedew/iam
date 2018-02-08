package it.infn.mw.iam.audit.events.aup;

import it.infn.mw.iam.persistence.model.IamAup;

public class AupUpdatedEvent extends AupEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AupUpdatedEvent(Object source, IamAup aup) {
    super(source, "AUP updated", aup);
  }

}
