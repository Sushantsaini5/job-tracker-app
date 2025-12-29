package com.jobtracker.controller;

import com.jobtracker.entity.User;
import com.jobtracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for admin operations
 * Only accessible by users with ADMIN role
 */
@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get all users (Admin only)
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserSummary>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        
        List<UserSummary> userSummaries = users.stream()
            .map(user -> new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getJobApplications().size()
            ))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(userSummaries);
    }
    
    /**
     * Get user by ID (Admin only)
     * GET /api/admin/users/{id}
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserSummary> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        
        UserSummary userSummary = new UserSummary(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.getJobApplications().size()
        );
        
        return ResponseEntity.ok(userSummary);
    }
    
    /**
     * User summary DTO for admin view
     */
    public static class UserSummary {
        public Long id;
        public String username;
        public String email;
        public String role;
        public java.time.LocalDateTime createdAt;
        public int jobApplicationCount;
        
        public UserSummary(Long id, String username, String email, String role, 
                          java.time.LocalDateTime createdAt, int jobApplicationCount) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.role = role;
            this.createdAt = createdAt;
            this.jobApplicationCount = jobApplicationCount;
        }
    }
}