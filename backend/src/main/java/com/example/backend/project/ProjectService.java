package com.example.backend.project;

import com.example.backend.auth.service.AuthService;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProject;
import com.example.backend.user_project.UserProjectId;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectConverter converter;
    private final UserService userService;
    private final UserProjectService userProjectService;
    private final AuthService authService;

//    public Project updateProject(UpdateProjectRequest request) {
//        Project project = converter.project(request);
//        return repository.save(project);
//    }

    public Optional<Project> findById(String id) {
        return repository.findById(id);
    }

    public CreateProjectResponse createProject(CreateProjectRequest request) {
        User user = userService.findById(authService.getCurrentUserId()).orElseThrow();
        Project project = converter.project(request);

        project = repository.save(project);

        UserProject.builder().build();
        UserProject userProject = UserProject.builder()
                .user(user)
                .project(project)
                .build();

        UserProject savedUserProject = userProjectService.save(userProject);
        return converter.createProjectResponse(savedUserProject.getProject());
    }

    public GetProjectResponse getProjectInfo(String projectId) {
        String userId = authService.getCurrentUserId();
        UserProject userProject = userProjectService.findById(
                UserProjectId.builder()
                        .userId(userId)
                        .projectId(projectId)
                        .build()
        ).orElseThrow();
        return converter.getProjectResponse(userProject.getProject());
    }

    public List<GetProjectResponse> getProjectsByUserId() {
        String userId = authService.getCurrentUserId();
        List<Project> projectList = userProjectService.getProjectsByUserId(userId);
        List<GetProjectResponse> responseList = new ArrayList<>();
        for (Project project : projectList) {
            responseList.add(converter.getProjectResponse(project));
        }
        return responseList;
    }
}
