package it.infn.mw.iam.persistence.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.infn.mw.iam.core.IamMessageStatus;
import it.infn.mw.iam.core.IamMessageType;

@Entity
@Table(name = "iam_messages")
public class IamMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 36, unique = true)
  private String uuid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date creationTime;

  @OneToOne
  @JoinColumn(name = "request_id")
  private IamRegistrationRequest request;

  @Enumerated(EnumType.STRING)
  @Column(name = "message_type")
  private IamMessageType messageType;

  @Enumerated(EnumType.STRING)
  @Column(name = "message_status")
  private IamMessageStatus messageStatus;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = true)
  private Date lastUpdate;

  public IamMessage() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Date getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(Date creationTime) {
    this.creationTime = creationTime;
  }

  public IamRegistrationRequest getRequest() {
    return request;
  }

  public void setRequest(IamRegistrationRequest request) {
    this.request = request;
  }

  public IamMessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(IamMessageType messageType) {
    this.messageType = messageType;
  }

  public IamMessageStatus getMessageStatus() {
    return messageStatus;
  }

  public void setMessageStatus(IamMessageStatus messageStatus) {
    this.messageStatus = messageStatus;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
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
    IamMessage other = (IamMessage) obj;
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
    return "IamMessage [id=" + id + ", uuid=" + uuid + ", creationTime=" + creationTime
        + ", request=" + request + ", messageType=" + messageType + ", messageStatus="
        + messageStatus + ", lastUpdate=" + lastUpdate + "]";
  }

}
