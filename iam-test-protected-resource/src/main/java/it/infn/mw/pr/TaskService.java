package it.infn.mw.pr;

import java.util.Collection;

public interface TaskService {
  
  void saveTask(Task t);
  Collection<Task> getTasks();
  Task getTaskById(String id);
  Task removeTaskById(String id);

}
