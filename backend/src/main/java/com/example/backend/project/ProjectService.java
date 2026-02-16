package com.example.backend.project;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProjectService {
    Optional<Project> findById(String id);

    @Transactional
    CreateProjectResponse createProject(String userId, CreateProjectRequest request);
}
