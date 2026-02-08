package com.example.backend.job;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateJobRequest {
    @NotEmpty
    private String name;
}
