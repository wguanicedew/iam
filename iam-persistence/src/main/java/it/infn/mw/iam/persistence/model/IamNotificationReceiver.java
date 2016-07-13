package it.infn.mw.iam.persistence.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "iam_notification_receiver")
public class IamNotificationReceiver {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "notification_id")
  private IamEmailNotification iamEmailNotification;

  @OneToOne
  @JoinColumn(name = "account_id")
  private IamAccount account;

  public IamNotificationReceiver() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public IamEmailNotification getIamEmailNotification() {
    return iamEmailNotification;
  }

  public void setIamEmailNotification(IamEmailNotification iamEmailNotification) {
    this.iamEmailNotification = iamEmailNotification;
  }

  public IamAccount getAccount() {
    return account;
  }

  public void setAccount(IamAccount account) {
    this.account = account;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    IamNotificationReceiver other = (IamNotificationReceiver) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "IamNotificationReceiver [id=" + id + ", iamEmailNotification=" + iamEmailNotification
        + ", account=" + account + "]";
  }

}
