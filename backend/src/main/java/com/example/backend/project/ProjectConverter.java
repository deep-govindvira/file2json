package com.example.backend.project;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectConverter {

//    private final ProjectService service;

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
                .projectYear(project.getYear())
                .totalMarksheets(project.getTotalMarksheets())
                .build();
    }

    public GetProjectResponse getProjectResponse(Project project) {
        return GetProjectResponse.builder()
                .projectDescription(project.getDescription())
                .processedMarksheets(project.getProcessedMarksheets())
                .totalMarksheets(project.getTotalMarksheets())
                .projectId(project.getId())
                .projectName(project.getName())
                .projectProcessingDuration(project.getProcessingDuration())
                .projectStatus(project.getStatus())
                .processingFailedMarksheets(project.getProcessingFailedMarksheets())
                .projectYear(project.getYear())
                .build();
    }

//    public Project project(UpdateProjectRequest request) {
//        Project project = service.findById(request.getId()).orElseThrow();
//        project.setYear(request.getProjectYear());
//        project.setDescription(request.getProjectDescription());
//        project.setName(request.getProjectName());
//        return project;
//    }
}
