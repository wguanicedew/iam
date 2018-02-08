package it.infn.mw.iam.audit.events.aup;

import it.infn.mw.iam.persistence.model.IamAup;

public class AupDeletedEvent extends AupEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AupDeletedEvent(Object source, IamAup aup) {
    super(source, "AUP deleted", aup);
  }

}
