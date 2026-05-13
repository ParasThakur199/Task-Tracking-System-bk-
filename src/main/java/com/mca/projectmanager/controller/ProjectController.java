package com.mca.projectmanager.controller;

import com.mca.projectmanager.entity.Project;
import com.mca.projectmanager.entity.User;
import com.mca.projectmanager.repository.ProjectRepository;
import com.mca.projectmanager.repository.UserRepository;
import com.mca.projectmanager.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<?> getUserProjects(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<Project> projects = projectRepository.findByUserId(userId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @PostMapping
    public ResponseEntity<?> createProject(@RequestHeader("Authorization") String token, @RequestBody Project project) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElseThrow();
            project.setUser(user);
            Project saved = projectRepository.save(project);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            Project project = projectRepository.findById(id).orElseThrow();
            if (project.getUser().getId().equals(userId)) {
                projectRepository.delete(project);
                return ResponseEntity.ok("Project deleted");
            }
            return ResponseEntity.status(403).body("Forbidden");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
    
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            List<Project> projects = projectRepository.findByUserId(userId);
            
            long totalProjects = projects.size();
            long totalTasks = 0;
            long completedTasks = 0;
            
            for(Project p : projects) {
                totalTasks += p.getTasks().size();
                completedTasks += p.getTasks().stream().filter(t -> "Completed".equals(t.getStatus())).count();
            }
            
            Map<String, Long> stats = new java.util.HashMap<>();
            stats.put("totalProjects", totalProjects);
            stats.put("totalTasks", totalTasks);
            stats.put("completedTasks", completedTasks);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
    }
}
