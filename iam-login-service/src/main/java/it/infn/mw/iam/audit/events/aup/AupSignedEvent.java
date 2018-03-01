package it.infn.mw.iam.audit.events.aup;

import static java.lang.String.format;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.infn.mw.iam.audit.events.IamAuditApplicationEvent;
import it.infn.mw.iam.audit.utils.IamAupSignatureSerializer;
import it.infn.mw.iam.persistence.model.IamAupSignature;

public class AupSignedEvent extends IamAuditApplicationEvent {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @JsonSerialize(using=IamAupSignatureSerializer.class)
  final IamAupSignature signature;
  
  public AupSignedEvent(Object source, IamAupSignature signature) {
    super(IamEventCategory.AUP, source, format("User %s signed the AUP", 
        signature.getAccount().getUsername()));
    this.signature = signature;
    
  }
  
  public IamAupSignature getSignature() {
    return signature;
  }

}
