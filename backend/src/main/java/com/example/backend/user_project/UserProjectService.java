package com.example.backend.user_project;

import java.util.Optional;

public interface UserProjectService {
    UserProject save(UserProject userProject);

    Optional<UserProject> findById(UserProjectId id);
}
