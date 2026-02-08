package com.example.backend.job;

import java.util.Optional;

public interface JobService {
    CreateJobResponse addJobToUser(String userId, CreateJobRequest request);

    Optional<Job> findById(String id);
}
