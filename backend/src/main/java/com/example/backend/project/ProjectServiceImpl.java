package com.example.backend.project;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProject;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectConverter converter;
    private final UserService userService;
    private final UserProjectService userProjectService;

    @Override
    public Optional<Project> findById(String id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public CreateProjectResponse createProject(String userId, CreateProjectRequest request) {
        User user = userService.findById(userId).orElseThrow();
        Project project = converter.project(request);

        Project savedProject = repository.save(project);

        UserProject.builder().build();
        UserProject userProject = UserProject.builder()
                .user(user)
                .project(savedProject)
                .build();

        UserProject savedUserProject = userProjectService.save(userProject);
        return converter.createProjectResponse(savedUserProject.getProject());
    }
}
