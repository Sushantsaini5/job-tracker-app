package com.jobtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JobTrackerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobTrackerBackendApplication.class, args);
		System.out.println("\n==============================================");
        System.out.println("Job Tracker Backend is running!");
        System.out.println("Server: http://localhost:8080");
        System.out.println("API Docs: Available via Postman Collection");
        System.out.println("==============================================\n");
	}

}
