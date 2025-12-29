// UserRepository.java
package com.jobtracker.repository;

import com.jobtracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity
 * Provides database operations for user management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username for authentication
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists (for registration validation)
    Boolean existsByUsername(String username);
    
    // Check if email exists (for registration validation)
    Boolean existsByEmail(String email);
}
