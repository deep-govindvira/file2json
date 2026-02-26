package com.example.backend.sse.dtos;

import com.example.backend.project.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStatusUpdateEvent {
    private String projectId;
    private ProjectStatus projectStatus;
    private Long projectProcessingDuration;
    private Integer processedMarksheets;
    private Integer processingFailedMarksheets;
    private Integer totalMarksheets;
}
