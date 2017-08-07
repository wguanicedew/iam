package it.infn.mw.iam.audit;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;

@FunctionalInterface
public interface AuditEventLogger {
  
  public void logAuditEvent(IamAuditApplicationEvent event);
}
