package it.infn.mw.iam.audit;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
@FunctionalInterface
public interface AuditDataSerializer {

  public String serialize(IamAuditApplicationEvent event);
  
}
