package com.example.backend.job;

import com.example.backend.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateJobResponse {
    private String id;
    private Status status;
}
