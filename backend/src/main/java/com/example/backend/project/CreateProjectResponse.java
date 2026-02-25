package com.example.backend.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateProjectResponse {
    private String projectId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private Integer projectYear;
    private Integer processedMarksheets;
    private Integer totalMarksheets;
}
