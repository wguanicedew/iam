package it.infn.mw.iam.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="saml_account")
public class SamlAccount {

  @Id
  @GeneratedValue
  private Long id;

  @OneToOne(mappedBy="samlAccount")
  IamAccount account;

  @Column(nullable = false, length = 256)
  String idpId;

  @Column(nullable = false, length = 256)
  String userId;

  public SamlAccount() {
  }

  public Long getId() {

    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public IamAccount getAccount() {

    return account;
  }

  public void setAccount(IamAccount account) {

    this.account = account;
  }

  public String getIdpId() {

    return idpId;
  }

  public void setIdpId(String idpId) {

    this.idpId = idpId;
  }

  public String getUserId() {

    return userId;
  }

  public void setUserId(String userId) {

    this.userId = userId;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((account == null) ? 0 : account.hashCode());
    result = prime * result + ((idpId == null) ? 0 : idpId.hashCode());
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
    SamlAccount other = (SamlAccount) obj;
    if (account == null) {
      if (other.account != null)
        return false;
    } else if (!account.equals(other.account))
      return false;
    if (idpId == null) {
      if (other.idpId != null)
        return false;
    } else if (!idpId.equals(other.idpId))
      return false;
    if (userId == null) {
      if (other.userId != null)
        return false;
    } else if (!userId.equals(other.userId))
      return false;
    return true;
  }

}
