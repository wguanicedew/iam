package it.infn.mw.iam.persistence.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "iam_x509_cert")
public class IamX509Certificate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 36)
  private String label;

  @Column(nullable = false, length = 128, unique = true)
  private String certificateSubject;

  @Lob
  @Column(nullable = false, unique = true)
  private String certificate;

  @Column(name = "is_primary")
  private boolean primary;

  @ManyToOne
  private IamAccount account;

  public IamX509Certificate() {
  }

  @Override
  public boolean equals(Object obj) {

	if (this == obj)
	  return true;
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	IamX509Certificate other = (IamX509Certificate) obj;
	if (account == null) {
	  if (other.account != null)
		return false;
	} else if (!account.equals(other.account))
	  return false;
	if (certificate == null) {
	  if (other.certificate != null)
		return false;
	} else if (!certificate.equals(other.certificate))
	  return false;
	if (id == null) {
	  if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	  return false;
	if (label == null) {
	  if (other.label != null)
		return false;
	} else if (!label.equals(other.label))
	  return false;
	if (primary != other.primary)
	  return false;
	return true;
  }

  public IamAccount getAccount() {

	return account;
  }

  public String getCertificateSubject() {

	return certificateSubject;
  }

  public String getCertificate() {

	return certificate;
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
	result = prime * result + ((account == null) ? 0 : account.hashCode());
	result = prime * result
	  + ((certificate == null) ? 0 : certificate.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + ((label == null) ? 0 : label.hashCode());
	result = prime * result + (primary ? 1231 : 1237);
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

  public void setCertificate(String certificate) {

	this.certificate = certificate;
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
