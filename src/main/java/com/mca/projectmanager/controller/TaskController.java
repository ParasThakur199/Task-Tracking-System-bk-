package com.mca.projectmanager.controller;

import com.mca.projectmanager.entity.Project;
import com.mca.projectmanager.entity.Task;
import com.mca.projectmanager.entity.User;
import com.mca.projectmanager.repository.ProjectRepository;
import com.mca.projectmanager.repository.TaskRepository;
import com.mca.projectmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<?> getTasksByProject(@PathVariable Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTasksByUser(@PathVariable Long userId) {
        List<Task> tasks = taskRepository.findByAssignedUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/project/{projectId}")
    public ResponseEntity<?> addTask(@PathVariable Long projectId, @RequestBody Task task) {
        Project project = projectRepository.findById(projectId).orElseThrow();
        task.setProject(project);
        if (task.getStatus() == null) {
            task.setStatus("Pending");
        }
        if (task.getAssignedUser() != null && task.getAssignedUser().getId() != null) {
            User assignedUser = userRepository.findById(task.getAssignedUser().getId()).orElse(null);
            task.setAssignedUser(assignedUser);
        }
        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDueDate(taskDetails.getDueDate());
        task.setStatus(taskDetails.getStatus());
        
        if (taskDetails.getAssignedUser() != null && taskDetails.getAssignedUser().getId() != null) {
            User assignedUser = userRepository.findById(taskDetails.getAssignedUser().getId()).orElse(null);
            task.setAssignedUser(assignedUser);
        }

        Task updated = taskRepository.save(task);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> statusMap) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus(statusMap.get("status"));
        Task updated = taskRepository.save(task);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return ResponseEntity.ok("Task deleted");
    }
}
