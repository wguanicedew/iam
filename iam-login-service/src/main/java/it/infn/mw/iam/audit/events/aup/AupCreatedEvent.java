package it.infn.mw.iam.audit.events.aup;

import it.infn.mw.iam.persistence.model.IamAup;

public class AupCreatedEvent extends AupEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public AupCreatedEvent(Object source, IamAup aup) {
    super(source, "AUP created", aup);
  }

}
