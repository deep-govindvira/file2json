package com.example.backend.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetProjectResponse {
    private String projectId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private Long projectProcessingDuration;
    private Integer projectYear;
    private Integer processedMarksheets;
    private Integer processingFailedMarksheets;
    private Integer totalMarksheets;
}
