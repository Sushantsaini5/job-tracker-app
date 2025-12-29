package com.jobtracker.service;

import com.jobtracker.dto.JobApplicationRequest;
import com.jobtracker.dto.JobApplicationResponse;
import com.jobtracker.dto.PageResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.JobApplication.ApplicationStatus;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Job Application operations
 * Handles business logic for job application management
 */
@Service
@Transactional
public class JobApplicationService {
    
    @Autowired
    private JobApplicationRepository jobApplicationRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new job application
     */
    public JobApplicationResponse createJobApplication(JobApplicationRequest request, String username) {
        User user = userService.findByUsername(username);
        
        JobApplication jobApplication = new JobApplication();
        jobApplication.setTitle(request.getTitle());
        jobApplication.setCompany(request.getCompany());
        jobApplication.setStatus(request.getStatus());
        jobApplication.setAppliedDate(request.getAppliedDate());
        jobApplication.setDeadline(request.getDeadline());
        jobApplication.setNotes(request.getNotes());
        jobApplication.setUser(user);
        
        JobApplication saved = jobApplicationRepository.save(jobApplication);
        return mapToResponse(saved);
    }
    
    /**
     * Update an existing job application
     */
    public JobApplicationResponse updateJobApplication(Long id, JobApplicationRequest request, String username) {
        User user = userService.findByUsername(username);
        
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
        
        jobApplication.setTitle(request.getTitle());
        jobApplication.setCompany(request.getCompany());
        jobApplication.setStatus(request.getStatus());
        jobApplication.setAppliedDate(request.getAppliedDate());
        jobApplication.setDeadline(request.getDeadline());
        jobApplication.setNotes(request.getNotes());
        
        JobApplication updated = jobApplicationRepository.save(jobApplication);
        return mapToResponse(updated);
    }
    
    /**
     * Get job application by ID
     */
    @Transactional(readOnly = true)
    public JobApplicationResponse getJobApplicationById(Long id, String username) {
        User user = userService.findByUsername(username);
        
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
        
        return mapToResponse(jobApplication);
    }
    
    /**
     * Get all job applications for a user with pagination, filtering, sorting, and search
     */
    @Transactional(readOnly = true)
    public PageResponse<JobApplicationResponse> getJobApplications(
            String username,
            ApplicationStatus status,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size,
            String sortBy,
            String sortDir) {
        
        User user = userService.findByUsername(username);
        
        // Create sort object
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Execute query with filters
        Page<JobApplication> jobApplicationPage = jobApplicationRepository.findByUserIdWithFilters(
            user.getId(), status, keyword, startDate, endDate, pageable
        );
        
        // Map to response DTOs
        List<JobApplicationResponse> content = jobApplicationPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
            content,
            jobApplicationPage.getNumber(),
            jobApplicationPage.getSize(),
            jobApplicationPage.getTotalElements(),
            jobApplicationPage.getTotalPages(),
            jobApplicationPage.isLast()
        );
    }
    
    /**
     * Delete job application
     */
    public void deleteJobApplication(Long id, String username) {
        User user = userService.findByUsername(username);
        
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found with id: " + id));
        
        jobApplicationRepository.delete(jobApplication);
    }
    
    /**
     * Get statistics for user's job applications
     */
    @Transactional(readOnly = true)
    public JobApplicationStats getStatistics(String username) {
        User user = userService.findByUsername(username);
        
        Long total = jobApplicationRepository.countByUserId(user.getId());
        Long applied = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.APPLIED);
        Long screening = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.SCREENING);
        Long interview = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.INTERVIEW);
        Long offer = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.OFFER);
        Long accepted = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.ACCEPTED);
        Long rejected = jobApplicationRepository.countByUserIdAndStatus(user.getId(), ApplicationStatus.REJECTED);
        
        return new JobApplicationStats(total, applied, screening, interview, offer, accepted, rejected);
    }
    
    /**
     * Map entity to response DTO
     */
    private JobApplicationResponse mapToResponse(JobApplication jobApplication) {
        return new JobApplicationResponse(
            jobApplication.getId(),
            jobApplication.getTitle(),
            jobApplication.getCompany(),
            jobApplication.getStatus(),
            jobApplication.getAppliedDate(),
            jobApplication.getDeadline(),
            jobApplication.getNotes(),
            jobApplication.getUser().getId(),
            jobApplication.getUser().getUsername(),
            jobApplication.getCreatedAt(),
            jobApplication.getUpdatedAt()
        );
    }
    
    /**
     * Inner class for statistics
     */
    public static class JobApplicationStats {
        public Long total;
        public Long applied;
        public Long screening;
        public Long interview;
        public Long offer;
        public Long accepted;
        public Long rejected;
        
        public JobApplicationStats(Long total, Long applied, Long screening, Long interview, 
                                  Long offer, Long accepted, Long rejected) {
            this.total = total;
            this.applied = applied;
            this.screening = screening;
            this.interview = interview;
            this.offer = offer;
            this.accepted = accepted;
            this.rejected = rejected;
        }
    }
}