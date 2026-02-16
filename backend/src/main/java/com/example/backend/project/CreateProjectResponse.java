package com.example.backend.project;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateProjectResponse {
    private String projectId;
    private String projectName;
    private String projectDescription;
    private ProjectStatus projectStatus;
    private LocalDateTime projectStatusUpdatedAt;
    private Integer projectYear;
    private Integer processedMarksheets;
    private Integer totalMarksheets;
}
