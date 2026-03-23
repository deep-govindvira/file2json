package com.example.backend.project;

import com.example.backend.auth.entity.Role;
import com.example.backend.auth.service.AuthService;
import com.example.backend.config.AppProps;
import com.example.backend.department.DepartmentService;
import com.example.backend.notification.NotificationPort;
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
import java.util.concurrent.ExecutorService;

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
    private final DepartmentService departmentService;
    private final NotificationPort notificationPort;
    private final ExecutorService executorService;

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

        boolean isNewUser = false;
        String tempPassword = null;

        if (user == null) {
            isNewUser = true;
            tempPassword = request.getEmail();

            user = User.builder()
                    .name(request.getEmail())
                    .email(request.getEmail())
                    .password(encoder.encode(request.getEmail()))
                    .department(departmentService.getDepartmentById(project.getProjectCreator().getDepartment().getId()))
                    .role(Role.VERIFIER)
                    .build();
            user = userService.save(user);
        } else {
            if (user.getRole().equals(Role.VERIFIER)) {
                user.setDepartment(departmentService.getDepartmentById(project.getProjectCreator().getDepartment().getId()));
                userService.save(user);
            }
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

        User finalUser = user;
        boolean finalIsNewUser = isNewUser;
        String finalTempPassword = tempPassword;

        executorService.submit(() -> {
            try {

                String projectLink = appProps.getAllowedOrigin() + "/project/" + projectId + "/view";
                String profileLink = appProps.getAllowedOrigin() + "/profile";

                String htmlMessage;

                if (finalIsNewUser) {

                    htmlMessage = """
                            <html>
                            <body style="font-family: Arial; background:#f5f6fa; padding:20px;">
                                <div style="max-width:600px;margin:auto;background:#fff;padding:24px;border-radius:10px;">
                            
                                    <h2>Welcome to the System</h2>
                            
                                    <p>Hello <b>%s</b>,</p>
                            
                                    <p>You have been added as a <b>Verifier</b> to the project:</p>
                                    <p><b>%s</b></p>
                            
                                    <div style="background:#f1f2f6;padding:15px;border-radius:8px;margin:20px 0;">
                                        <p><b>Email:</b> %s</p>
                                        <p><b>Temporary Password:</b> %s</p>
                                    </div>
                            
                                    <p style="color:#d63031;"><b>⚠️ Please change your password immediately.</b></p>
                            
                                    <div style="text-align:center;margin:25px 0;">
                                        <a href="%s" style="background:#0984e3;color:#fff;padding:12px 20px;
                                        text-decoration:none;border-radius:6px;">Change Password</a>
                                    </div>
                            
                                    <div style="text-align:center;margin:10px 0;">
                                        <a href="%s" style="color:#0984e3;">Open Project</a>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """.formatted(
                            finalUser.getName(),
                            project.getName(),
                            finalUser.getEmail(),
                            finalTempPassword,
                            profileLink,
                            projectLink
                    );

                } else {

                    htmlMessage = """
                            <html>
                            <body style="font-family: Arial; background:#f5f6fa; padding:20px;">
                                <div style="max-width:600px;margin:auto;background:#fff;padding:24px;border-radius:10px;">
                            
                                    <h2>Project Access Granted</h2>
                            
                                    <p>Hello <b>%s</b>,</p>
                            
                                    <p>You have been added to the project:</p>
                                    <p><b>%s</b></p>
                            
                                    <div style="text-align:center;margin:25px 0;">
                                        <a href="%s" style="background:#0984e3;color:#fff;padding:12px 20px;
                                        text-decoration:none;border-radius:6px;">View Project</a>
                                    </div>
                            
                                </div>
                            </body>
                            </html>
                            """.formatted(
                            finalUser.getName(),
                            project.getName(),
                            projectLink
                    );
                }

                notificationPort.notify(
                        "Project Access - " + project.getName(),
                        finalUser.getEmail(),
                        htmlMessage
                );

            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        });
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

    public GetProjectResponseForVerifier getProjectInfoForVerifier(String projectId) {
        String userId = authService.getCurrentUserId();
        UserProject userProject = userProjectService.findById(
                UserProjectId.builder()
                        .userId(UUID.fromString(userId))
                        .projectId(UUID.fromString(projectId))
                        .build()
        ).orElseThrow();
        return converter.getProjectResponseForVerifier(userProject.getProject());
    }

    public List<GetProjectResponseForVerifier> getProjectsByUserIdForVerifier() {
        String userId = authService.getCurrentUserId();

        List<Project> projectList = userProjectService.getProjectsByUserId(userId);
        List<GetProjectResponseForVerifier> responseList = new ArrayList<>();
        for (Project project : projectList) {
            responseList.add(converter.getProjectResponseForVerifier(project));
        }
        return responseList;
    }
}
