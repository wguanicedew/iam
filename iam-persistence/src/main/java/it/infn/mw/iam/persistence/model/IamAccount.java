package it.infn.mw.iam.persistence.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 
 *
 */
@Entity
@Table(name = "iam_account")
public class IamAccount {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 36, unique = true)
  private String uuid;

  @Column(nullable = false, length = 128, unique = true)
  @NotNull
  private String username;

  @Column(length = 128)
  private String password;

  @OneToOne
  @JoinColumn(name = "iam_user_info_id")
  private IamAccountUserInfo userInfo;

  @ManyToMany
  @JoinTable(name = "iam_account_authority",
  joinColumns = @JoinColumn(name = "iam_account_id",
    referencedColumnName = "id"),
  inverseJoinColumns = @JoinColumn(name = "iam_authority_id",
    referencedColumnName = "id"))
  private Set<IamAuthority> authorities;

  @ManyToMany
  @JoinTable(name = "iam_account_group",
    joinColumns = @JoinColumn(name = "iam_account_id",
      referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "iam_group_id",
      referencedColumnName = "id"))
  private Set<IamGroup> groups;

  @OneToOne(optional = true)
  private IamSamlAccount samlAccount;

  @OneToOne(optional = true)
  @JoinColumn(name = "oidc_account_id")
  private IamOidcAccount oidcAccount;

  protected IamAccount() {
  }

  public Long getId() {

    return id;
  }

  public void setId(final Long id) {

    this.id = id;
  }

  public String getUuid() {

    return uuid;
  }

  public void setUuid(final String uuid) {

    this.uuid = uuid;
  }

  public String getUsername() {

    return username;
  }

  public void setUsername(final String username) {

    this.username = username;
  }

  public String getPassword() {

    return password;
  }

  public void setPassword(final String password) {

    this.password = password;
  }

  public IamAccountUserInfo getUserInfo() {

    return userInfo;
  }

  public void setUserInfo(final IamAccountUserInfo userInfo) {

    this.userInfo = userInfo;
  }

  public Set<IamAuthority> getAuthorities() {

    return authorities;
  }

  public void setAuthorities(final Set<IamAuthority> authorities) {

    this.authorities = authorities;
  }

  public IamSamlAccount getSamlAccount() {

    return samlAccount;
  }

  public void setSamlAccount(final IamSamlAccount samlAccount) {

    this.samlAccount = samlAccount;
  }

  public Set<IamGroup> getGroups() {

    return groups;
  }

  public void setGroups(final Set<IamGroup> groups) {

    this.groups = groups;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
    IamAccount other = (IamAccount) obj;
    if (uuid == null) {
      if (other.uuid != null)
        return false;
    } else if (!uuid.equals(other.uuid))
      return false;
    return true;
  }

}
