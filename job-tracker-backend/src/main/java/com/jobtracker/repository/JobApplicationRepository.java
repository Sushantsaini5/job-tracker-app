// JobApplicationRepository.java
package com.jobtracker.repository;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobApplication.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Repository interface for JobApplication entity
 * Provides database operations with pagination, filtering, and search
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    // Find all job applications for a specific user with pagination
    Page<JobApplication> findByUserId(Long userId, Pageable pageable);
    
    // Find job application by ID and user ID (ensures users can only access their own data)
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
    
    // Filter by status for a specific user
    Page<JobApplication> findByUserIdAndStatus(Long userId, ApplicationStatus status, Pageable pageable);
    
    // Filter by date range for a specific user
    Page<JobApplication> findByUserIdAndAppliedDateBetween(
        Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable
    );
    
    // Search by title or company name (case-insensitive) for a specific user
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user.id = :userId AND " +
           "(LOWER(ja.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ja.company) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<JobApplication> searchByTitleOrCompany(
        @Param("userId") Long userId, 
        @Param("keyword") String keyword, 
        Pageable pageable
    );
    
    // Combined search and filter query
    @Query("SELECT ja FROM JobApplication ja WHERE ja.user.id = :userId " +
           "AND (:status IS NULL OR ja.status = :status) " +
           "AND (:keyword IS NULL OR LOWER(ja.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(ja.company) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:startDate IS NULL OR ja.appliedDate >= :startDate) " +
           "AND (:endDate IS NULL OR ja.appliedDate <= :endDate)")
    Page<JobApplication> findByUserIdWithFilters(
        @Param("userId") Long userId,
        @Param("status") ApplicationStatus status,
        @Param("keyword") String keyword,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    // Count applications by user
    Long countByUserId(Long userId);
    
    // Count applications by user and status
    Long countByUserIdAndStatus(Long userId, ApplicationStatus status);
}