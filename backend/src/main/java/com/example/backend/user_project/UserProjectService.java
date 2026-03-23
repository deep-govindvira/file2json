package com.example.backend.user_project;

import com.example.backend.project.Project;
import com.example.backend.user.User;

import java.util.List;
import java.util.Optional;


public interface UserProjectService {

    void deleteById(UserProjectId id);

    UserProject save(UserProject userProject);

    Optional<UserProject> findById(UserProjectId id);

    List<Project> getProjectsByUserId(String userId);

    List<User> getUsersByProjectId(String projectId);
}
