package com.example.backend.user_project;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProjectServiceImpl implements UserProjectService {

    private final UserProjectRepository repository;

    @Override
    public UserProject save(UserProject userProject) {
        return repository.save(userProject);
    }

    @Override
    public Optional<UserProject> findById(UserProjectId id) {
        return repository.findById(id);
    }
}
