package it.infn.mw.iam.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "iam_x509_cert")
public class IamX509Certificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 36, unique = true)
  private String label;

  @Column(nullable = false, length = 128, unique = true)
  private String certificateSubject;

  @Column(name = "is_primary")
  private boolean primary;

  @ManyToOne
  private IamAccount account;

  public IamX509Certificate() {}

  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamX509Certificate other = (IamX509Certificate) obj;
    if (certificateSubject == null) {
      if (other.certificateSubject != null)
        return false;
    } else if (!certificateSubject.equals(other.certificateSubject))
      return false;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    return true;
  }

  public IamAccount getAccount() {

    return account;
  }

  public String getCertificateSubject() {

    return certificateSubject;
  }

  public Long getId() {

    return id;
  }

  public String getLabel() {

    return label;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((certificateSubject == null) ? 0 : certificateSubject.hashCode());
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    return result;
  }

  public boolean isPrimary() {

    return primary;
  }

  public void setAccount(IamAccount account) {

    this.account = account;
  }

  public void setCertificateSubject(String certificateSubject) {

    this.certificateSubject = certificateSubject;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public void setLabel(String label) {

    this.label = label;
  }

  public void setPrimary(boolean primary) {

    this.primary = primary;
  }

}
