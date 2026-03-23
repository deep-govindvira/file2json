package com.example.backend.project;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectService {

    Project updateProject(UUID projectId, UpdateProjectRequest request);

    void addUserToProject(UUID projectId, AccessForUserToProjectRequest request);

    void removeUserFromProject(UUID projectId, AccessForUserToProjectRequest request);

    @Transactional
    Project refreshProjectStatistics(String projectId);

    Optional<Project> findById(String id);

    CreateProjectResponse createProject(CreateProjectRequest request);

    GetProjectResponse getProjectInfo(String projectId);

    List<GetProjectResponse> getProjectsByUserId();

    GetProjectResponseForVerifier getProjectInfoForVerifier(String projectId);

    List<GetProjectResponseForVerifier> getProjectsByUserIdForVerifier();
}
