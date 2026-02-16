package com.example.backend.project;


import org.springframework.stereotype.Component;

@Component
public class ProjectConverter {
    public Project project(CreateProjectRequest request) {
        Project project = new Project();
        project.setYear(request.getProjectYear());
        project.setDescription(request.getProjectDescription());
        project.setName(request.getProjectName());
        return project;
    }

    public CreateProjectResponse createProjectResponse(Project project) {
        return CreateProjectResponse.builder()
                .processedMarksheets(project.getProcessedMarksheets())
                .projectDescription(project.getDescription())
                .projectId(project.getId())
                .projectName(project.getName())
                .projectStatus(project.getStatus())
                .projectStatusUpdatedAt(project.getStatusUpdatedAt())
                .projectYear(project.getYear())
                .totalMarksheets(project.getTotalMarksheets())
                .build();
    }
}
