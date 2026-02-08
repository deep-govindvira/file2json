package com.example.backend.job;

public class JobConverter {
    public static Job toJob(CreateJobRequest request) {
        return Job.builder()
                .name(request.getName())
                .build();
    }

    public static CreateJobResponse toCreateJobResponse(Job job) {
        return CreateJobResponse.builder()
                .name(job.getName())
                .id(job.getId())
                .status(job.getStatus())
                .build();
    }
}
