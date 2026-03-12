package com.example.backend.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProjectResponseForVerifier {
    private String projectId;
    private String projectName;
    private String projectDescription;
}
