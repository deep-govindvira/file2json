package com.example.backend.project;

import com.example.backend.auth.entity.Role;
import com.example.backend.auth.service.AuthService;
import com.example.backend.config.AppProps;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProject;
import com.example.backend.user_project.UserProjectId;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectConverter converter;
    private final UserService userService;
    private final UserProjectService userProjectService;
    private final AuthService authService;
    private final AppProps appProps;
    private final PasswordEncoder encoder;

    public Project updateProject(UUID projectId, UpdateProjectRequest request) {
        String creatorUserId = authService.getCurrentUserId();

        Project project = repository.findById(projectId).orElseThrow();

        if (!project.getProjectCreator().getId().equals(UUID.fromString(creatorUserId))) {
            throw new RuntimeException("User is not creator of project.");
        }

        project.setName(request.getProjectName());
        project.setYear(request.getProjectYear());
        project.setDescription(request.getProjectDescription());
        return repository.save(project);
    }

    public void addUserToProject(UUID projectId, AccessForUserToProjectRequest request) {
        String creatorUserId = authService.getCurrentUserId();

        Project project = repository.findById(projectId).orElseThrow();

        if (!project.getProjectCreator().getId().equals(UUID.fromString(creatorUserId))) {
            throw new RuntimeException("User is not creator of project.");
        }

        User user = userService.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            user = User.builder()
                    .name(request.getEmail())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getEmail()))
                    .department("UNKNOWN")
                    .role(Role.ADMIN)
                    .build();
            user = userService.save(user);
        }


        UserProjectId id = new UserProjectId();
        id.setUserId(user.getId());
        id.setProjectId(project.getId());

        UserProject userProject = UserProject.builder()
                .id(id)
                .user(user)
                .project(project)
                .build();

        userProjectService.save(userProject);
    }

    public void removeUserFromProject(UUID projectId, AccessForUserToProjectRequest request) {
        String creatorUserId = authService.getCurrentUserId();

        Project project = repository.findById(projectId).orElseThrow();

        if (!project.getProjectCreator().getId().equals(UUID.fromString(creatorUserId))) {
            throw new RuntimeException("User is not creator of project.");
        }

        User user = userService.findByEmail(request.getEmail()).orElseThrow(() ->
                new RuntimeException("User not found.")
        );

        // Prevent removing project creator
        if (project.getProjectCreator().getId().equals(user.getId())) {
            throw new RuntimeException("Project creator cannot be removed from the project.");
        }

        UserProjectId id = new UserProjectId();
        id.setUserId(user.getId());
        id.setProjectId(project.getId());

        userProjectService.deleteById(id);
    }

    @Transactional
    public Project refreshProjectStatistics(String projectId) {
        repository.refreshProjectStatistics(UUID.fromString(projectId), Long.valueOf(appProps.getNoOfThreads()));
        return repository.findById(UUID.fromString(projectId)).orElseThrow();
    }

    public Optional<Project> findById(String id) {
        return repository.findById(UUID.fromString(id));
    }

    public CreateProjectResponse createProject(CreateProjectRequest request) {
        User user = userService.findById(authService.getCurrentUserId()).orElseThrow();
        Project project = converter.project(request);
        project.setProjectCreator(user);

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
                        .userId(UUID.fromString(userId))
                        .projectId(UUID.fromString(projectId))
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
