package it.infn.mw.iam.persistence.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "iam_scope_policy")
public class IamScopePolicy {

  public enum Rule {
    PERMIT,
    DENY
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="description", nullable = true, length = 512)
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="creation_time", nullable = false)
  private Date creationTime;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="last_update_time", nullable = false)
  private Date lastUpdateTime;

  @Enumerated(EnumType.STRING)
  @Column(name="rule", nullable = false, length = 6)
  private Rule rule;

  @ManyToOne(optional = true)
  @JoinColumn(name = "group_id")
  private IamGroup group;

  @ManyToMany
  @JoinTable(name = "iam_scope_policy_scope",
    joinColumns = @JoinColumn(name = "policy_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "scope_id", referencedColumnName = "id"))
  private Set<IamScope> scopes = new HashSet<>();

  public IamScopePolicy() {
    // empty constructor
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public Date getLastUpdateTime() {
    return lastUpdateTime;
  }

  public void setLastUpdateTime(Date lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public Rule getRule() {
    return rule;
  }

  public void setRule(Rule rule) {
    this.rule = rule;
  }

  public IamGroup getGroup() {
    return group;
  }

  public void setGroup(IamGroup group) {
    this.group = group;
  }

  public Set<IamScope> getScopes() {
    return scopes;
  }

  public void setScopes(Set<IamScope> scopes) {
    this.scopes = scopes;
  }

}
