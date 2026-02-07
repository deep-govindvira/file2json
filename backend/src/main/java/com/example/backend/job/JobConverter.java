package com.example.backend.job;

public class JobConverter {
    public static CreateJobResponse toCreateJobResponse(Job job) {
        return CreateJobResponse.builder()
                .id(job.getId())
                .status(job.getStatus())
                .build();
    }
}
