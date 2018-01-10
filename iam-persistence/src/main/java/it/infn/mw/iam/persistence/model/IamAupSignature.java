package it.infn.mw.iam.persistence.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "iam_aup_signature",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"aup_id", "account_id"})})
public class IamAupSignature implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 2948891215385302461L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "aup_id")
  IamAup aup;

  @ManyToOne(optional = false)
  @JoinColumn(name = "account_id")
  IamAccount account;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "signature_time", nullable = false)
  Date signatureTime;

  public IamAupSignature() {
    // empty constructor
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    result = prime * result + ((aup == null) ? 0 : aup.hashCode());
    result = prime * result + ((signatureTime == null) ? 0 : signatureTime.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamAupSignature other = (IamAupSignature) obj;
    if (account == null) {
      if (other.account != null)
        return false;
    } else if (!account.equals(other.account))
      return false;
    if (aup == null) {
      if (other.aup != null)
        return false;
    } else if (!aup.equals(other.aup))
      return false;
    if (signatureTime == null) {
      if (other.signatureTime != null)
        return false;
    } else if (!signatureTime.equals(other.signatureTime))
      return false;
    return true;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IamAup getAup() {
    return aup;
  }

  public void setAup(IamAup aup) {
    this.aup = aup;
  }

  public IamAccount getAccount() {
    return account;
  }

  public void setAccount(IamAccount account) {
    this.account = account;
  }

  public Date getSignatureTime() {
    return signatureTime;
  }

  public void setSignatureTime(Date signatureTime) {
    this.signatureTime = signatureTime;
  }

}
