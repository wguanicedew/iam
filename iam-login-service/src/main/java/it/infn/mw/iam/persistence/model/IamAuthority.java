package it.infn.mw.iam.persistence.model;

import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Table(name = "iam_authority")
public class IamAuthority {

  @Id
  @GeneratedValue
  Long id;

  @Column(name = "auth", length = 128, nullable = false, unique = true)
  String authority;

  public Long getId() {

    return id;
  }

  public void setId(Long id) {

    this.id = id;
  }

  public String getAuthority() {

    return authority;
  }

  public void setAuthority(String authority) {

    this.authority = authority;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((authority == null) ? 0 : authority.hashCode());
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
    IamAuthority other = (IamAuthority) obj;
    if (authority == null) {
      if (other.authority != null)
        return false;
    } else if (!authority.equals(other.authority))
      return false;
    return true;
  }

}
