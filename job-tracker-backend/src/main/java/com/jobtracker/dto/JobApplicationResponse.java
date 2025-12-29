// JobApplicationResponse.java
package com.jobtracker.dto;

import com.jobtracker.entity.JobApplication.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationResponse {
    private Long id;
    private String title;
    private String company;
    private ApplicationStatus status;
    private LocalDate appliedDate;
    private LocalDate deadline;
    private String notes;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
