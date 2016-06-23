package it.infn.mw.pr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class InMemoryTaskService implements TaskService {

  private final Map<String, Task> taskStore = new HashMap<>();

  @Override
  public void saveTask(Task t) {

    taskStore.put(t.getId(), t);

  }

  @Override
  public Collection<Task> getTasks() {

    return taskStore.values();
  }

  @Override
  public Task getTaskById(String id) {

    return taskStore.get(id);
  }

  @Override
  public Task removeTaskById(String id) {

    Task t = taskStore.get(id);
    if (t == null) {
      return null;
    }

    taskStore.remove(id);
    return t;
  }
}
