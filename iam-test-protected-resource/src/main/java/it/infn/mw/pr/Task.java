package it.infn.mw.pr;

import java.util.Date;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Task {

  private final String id;

  @NotNull
  @Size(min = 1, max = 100)
  private final String description;

  private boolean done = false;

  private final Date creationDate;
  private Date completionDate;

  public Task(String description) {
    this.id = UUID.randomUUID().toString();
    this.description = description;
    this.creationDate = new Date();
  }

  public void complete() {

    setDone(true);
    setCompletionDate(new Date());
  }

  public Date getCompletionDate() {

    return completionDate;
  }

  public Date getCreationDate() {

    return creationDate;
  }

  public String getDescription() {

    return description;
  }

  public String getId() {

    return id;
  }

  public boolean isDone() {

    return done;
  }

  public void setCompletionDate(Date completionDate) {

    this.completionDate = completionDate;
  }

  public void setDone(boolean done) {

    this.done = done;
  }
}
