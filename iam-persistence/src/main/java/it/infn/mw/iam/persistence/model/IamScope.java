package it.infn.mw.iam.persistence.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "iam_scope")
public class IamScope implements Serializable{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 256, nullable = false, unique = true)
  private String scope;

  @ManyToMany(mappedBy="scopes")
  Set<IamScopePolicy> policies = new HashSet<>(); 
  
  public IamScope(String scope) {
    this.scope = scope;
  }

  public IamScope() {
    
  }
  
  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<IamScopePolicy> getPolicies() {
    return policies;
  }

  public void setPolicies(Set<IamScopePolicy> policies) {
    this.policies = policies;
  }

  @Override
  public String toString() {
    return "IamScope [scope=" + scope + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
    IamScope other = (IamScope) obj;
    if (scope == null) {
      if (other.scope != null)
        return false;
    } else if (!scope.equals(other.scope))
      return false;
    return true;
  }


}
