package it.infn.mw.iam.persistence.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mitre.oauth2.model.AuthenticationHolderEntity;

@Entity
@Table(name = "iam_ext_authn")
public class IamExternalAuthenticationDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  Long id;

  @OneToOne(optional = false)
  @JoinColumn(name = "holder_id")
  AuthenticationHolderEntity holder;

  @Column(name = "type", length = 32, nullable = false)
  String type;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, name = "authentication_time")
  Date authenticationTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, name = "expiration_time")
  Date expirationTime;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "details")
  List<IamExternalAuthenticationAttribute> attributes;

  public IamExternalAuthenticationDetails() {}

  public List<IamExternalAuthenticationAttribute> getAttributes() {
    return attributes;
  }


  public void setAttributes(List<IamExternalAuthenticationAttribute> attributes) {
    this.attributes = attributes;
  }

  public void addAttribute(String name, String value) {
    IamExternalAuthenticationAttribute attr = new IamExternalAuthenticationAttribute();
    attr.setName(name);
    attr.setValue(value);
    attr.setDetails(this);
    attributes.add(attr);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((authenticationTime == null) ? 0 : authenticationTime.hashCode());
    result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
    result = prime * result + ((holder == null) ? 0 : holder.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  public Long getId() {
    return id;
  }



  public void setId(Long id) {
    this.id = id;
  }



  public AuthenticationHolderEntity getHolder() {
    return holder;
  }



  public void setHolder(AuthenticationHolderEntity holder) {
    this.holder = holder;
  }



  public String getType() {
    return type;
  }



  public void setType(String type) {
    this.type = type;
  }



  public Date getAuthenticationTime() {
    return authenticationTime;
  }



  public void setAuthenticationTime(Date authenticationTime) {
    this.authenticationTime = authenticationTime;
  }



  public Date getExpirationTime() {
    return expirationTime;
  }



  public void setExpirationTime(Date expirationTime) {
    this.expirationTime = expirationTime;
  }



  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    IamExternalAuthenticationDetails other = (IamExternalAuthenticationDetails) obj;
    if (authenticationTime == null) {
      if (other.authenticationTime != null)
        return false;
    } else if (!authenticationTime.equals(other.authenticationTime))
      return false;
    if (expirationTime == null) {
      if (other.expirationTime != null)
        return false;
    } else if (!expirationTime.equals(other.expirationTime))
      return false;
    if (holder == null) {
      if (other.holder != null)
        return false;
    } else if (!holder.getId().equals(other.holder.getId()))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "IamExternalAuthenticationDetails [id=" + id + ", holder=" + holder + ", type=" + type
        + ", authenticationTime=" + authenticationTime + ", expirationTime=" + expirationTime
        + ", attributes=" + attributes + "]";
  }

}
