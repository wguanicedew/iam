package it.infn.mw.pr;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Tasks")
public class TaskController {

  @Autowired
  TaskService taskService;

  @PreAuthorize("#oauth2.hasScope('write-tasks') and hasRole('API')")
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ResponseEntity<Task> create(@RequestParam String description) {

    Task t = new Task(description);
    taskService.saveTask(t);
    return new ResponseEntity<Task>(t, HttpStatus.CREATED);
  }

  @PreAuthorize("#oauth2.hasScope('read-tasks') and hasRole('API')")
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ResponseEntity<Collection<Task>> getAllTasks() {

    return new ResponseEntity<Collection<Task>>(taskService.getTasks(), HttpStatus.OK);
  }

  @PreAuthorize("#oauth2.hasScope('write-tasks') and hasRole('API')")
  @RequestMapping(value = "/{id}", method = RequestMethod.POST)
  public ResponseEntity<Task> completeTask(@PathVariable("id") String id) {
    Task t = taskService.getTaskById(id);
    if (t == null) {
      return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
    }

    t.complete();
    return new ResponseEntity<Task>(t, HttpStatus.OK);
  }

  @PreAuthorize("#oauth2.hasScope('write-tasks') and hasRole('API')")
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Task> deleteTask(@PathVariable("id") String id) {
    Task t = taskService.removeTaskById(id);
    if (t == null) {
      return new ResponseEntity<Task>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<Task>(t, HttpStatus.OK);
  }
}
