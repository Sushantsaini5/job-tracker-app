// JobApplicationRequest.java
package com.jobtracker.dto;

import com.jobtracker.entity.JobApplication.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JobApplicationRequest {
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Company name is required")
    private String company;
    
    @NotNull(message = "Status is required")
    private ApplicationStatus status;
    
    @NotNull(message = "Applied date is required")
    private LocalDate appliedDate;
    
    private LocalDate deadline;
    
    private String notes;
}