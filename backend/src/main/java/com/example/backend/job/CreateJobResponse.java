package com.example.backend.job;

import com.example.backend.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateJobResponse {
    private String id;
    private String name;
    private Status status;
}
