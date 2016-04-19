package it.infn.mw.iam.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "oidc_account")
public class OidcAccount {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(mappedBy = "oidcAccount")
  IamAccount account;

  @Column(nullable = false, length = 256)
  String issuer;

  @Column(nullable = false, length = 256)
  String subject;

  public OidcAccount() {
  }

  public Long getId() {

    return id;
  }

  public void setId(final Long id) {

    this.id = id;
  }

  public IamAccount getAccount() {

    return account;
  }

  public void setAccount(final IamAccount account) {

    this.account = account;
  }

  public String getIssuer() {

    return issuer;
  }

  public void setIssuer(final String issuer) {

    this.issuer = issuer;
  }

  public String getSubject() {

    return subject;
  }

  public void setSubject(final String subject) {

    this.subject = subject;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    result = prime * result + ((issuer == null) ? 0 : issuer.hashCode());
    result = prime * result + ((subject == null) ? 0 : subject.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    OidcAccount other = (OidcAccount) obj;
    if (account == null) {
      if (other.account != null)
        return false;
    } else if (!account.equals(other.account))
      return false;
    if (issuer == null) {
      if (other.issuer != null)
        return false;
    } else if (!issuer.equals(other.issuer))
      return false;
    if (subject == null) {
      if (other.subject != null)
        return false;
    } else if (!subject.equals(other.subject))
      return false;
    return true;
  }

}
