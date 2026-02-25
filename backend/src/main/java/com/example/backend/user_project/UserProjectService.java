package com.example.backend.user_project;

import com.example.backend.project.Project;
import com.example.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProjectService {

    private final UserProjectRepository repository;

    public UserProject save(UserProject userProject) {
        return repository.save(userProject);
    }

    public Optional<UserProject> findById(UserProjectId id) {
        return repository.findById(id);
    }

    public List<Project> getProjectsByUserId(String userId) {
        return repository.findProjectsByUserId(userId);
    }

    public List<User> getUsersByProjectId(String projectId) {
        return repository.findUsersByProjectId(projectId);
    }
}
