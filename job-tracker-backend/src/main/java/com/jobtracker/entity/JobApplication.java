package com.jobtracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * JobApplication entity representing a job application record
 * Each application belongs to a specific user
 */
@Entity
@Table(name = "job_applications", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_applied_date", columnList = "applied_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false)
    private String company;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.APPLIED;
    
    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate;
    
    @Column(name = "deadline")
    private LocalDate deadline;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    // Many-to-One relationship with User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    /**
     * Application status enum
     */
    public enum ApplicationStatus {
        APPLIED,        // Initial application submitted
        SCREENING,      // Application under review
        INTERVIEW,      // Interview scheduled/completed
        OFFER,          // Offer received
        ACCEPTED,       // Offer accepted
        REJECTED,       // Application rejected
        WITHDRAWN       // Application withdrawn by applicant
    }
    
    // Constructor for creating new job applications
    public JobApplication(String title, String company, ApplicationStatus status, 
                         LocalDate appliedDate, LocalDate deadline, String notes, User user) {
        this.title = title;
        this.company = company;
        this.status = status;
        this.appliedDate = appliedDate;
        this.deadline = deadline;
        this.notes = notes;
        this.user = user;
    }
}