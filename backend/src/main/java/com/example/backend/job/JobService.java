package com.example.backend.job;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface JobService {
    ResponseEntity<CreateJobResponse> addJobToUser(String userId, CreateJobRequest request);

    Optional<Job> findById(String id);
}
