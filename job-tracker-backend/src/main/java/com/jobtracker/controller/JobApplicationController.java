package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.dto.JobApplicationRequest;
import com.jobtracker.dto.JobApplicationResponse;
import com.jobtracker.dto.PageResponse;
import com.jobtracker.entity.JobApplication.ApplicationStatus;
import com.jobtracker.service.JobApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for Job Application operations
 * Handles CRUD operations with pagination, filtering, sorting, and search
 * All operations are user-specific - users can only access their own job applications
 */
@RestController
@RequestMapping("/api/jobs")
//@CrossOrigin(origins = "*", maxAge = 3600)
public class JobApplicationController {
    
    @Autowired
    private JobApplicationService jobApplicationService;
    
    /**
     * Create a new job application
     * POST /api/jobs
     * 
     * @param request Job application details
     * @param authentication Current authenticated user
     * @return Created job application
     */
    @PostMapping
    public ResponseEntity<JobApplicationResponse> createJobApplication(
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        JobApplicationResponse response = jobApplicationService.createJobApplication(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get all job applications with pagination, filtering, sorting, and search
     * GET /api/jobs?page=0&size=10&sortBy=appliedDate&sortDir=desc&status=APPLIED&keyword=developer
     * 
     * @param page Page number (default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Field to sort by (default: appliedDate)
     * @param sortDir Sort direction - asc or desc (default: desc)
     * @param status Filter by application status (optional)
     * @param keyword Search in title or company (optional)
     * @param startDate Filter by applied date from (optional)
     * @param endDate Filter by applied date to (optional)
     * @param authentication Current authenticated user
     * @return Paginated list of job applications
     */
    @GetMapping
    public ResponseEntity<PageResponse<JobApplicationResponse>> getAllJobApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appliedDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        
        String username = authentication.getName();
        PageResponse<JobApplicationResponse> response = jobApplicationService.getJobApplications(
            username, status, keyword, startDate, endDate, page, size, sortBy, sortDir
        );
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get job application by ID
     * GET /api/jobs/{id}
     * 
     * @param id Job application ID
     * @param authentication Current authenticated user
     * @return Job application details
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getJobApplicationById(
            @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        JobApplicationResponse response = jobApplicationService.getJobApplicationById(id, username);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update job application
     * PUT /api/jobs/{id}
     * 
     * @param id Job application ID
     * @param request Updated job application details
     * @param authentication Current authenticated user
     * @return Updated job application
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> updateJobApplication(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        JobApplicationResponse response = jobApplicationService.updateJobApplication(id, request, username);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete job application
     * DELETE /api/jobs/{id}
     * 
     * @param id Job application ID
     * @param authentication Current authenticated user
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteJobApplication(
            @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        jobApplicationService.deleteJobApplication(id, username);
        return ResponseEntity.ok(new ApiResponse(true, "Job application deleted successfully"));
    }
    
    /**
     * Get statistics for user's job applications
     * GET /api/jobs/stats
     * 
     * @param authentication Current authenticated user
     * @return Statistics including counts by status
     */
    @GetMapping("/stats")
    public ResponseEntity<JobApplicationService.JobApplicationStats> getStatistics(
            Authentication authentication) {
        
        String username = authentication.getName();
        JobApplicationService.JobApplicationStats stats = jobApplicationService.getStatistics(username);
        return ResponseEntity.ok(stats);
    }
}