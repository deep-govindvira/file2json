package com.example.backend.job;

import java.util.Optional;

public interface JobService {
    CreateJobResponse addJobToUser(String userId);

    Optional<Job> findById(String id);
}
