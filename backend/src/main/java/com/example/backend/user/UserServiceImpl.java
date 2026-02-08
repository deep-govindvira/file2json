package com.example.backend.user;

import com.example.backend.config.AppProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AppProps props;

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        User user = UserConverter.toUser(request);
        User savedUser = repository.save(user);
        createFolderForUser(savedUser);
        return UserConverter.toCreateUserResponse(savedUser);
    }

    private void createFolderForUser(User user) {
        Path newFolder = Paths.get(
                props.getUploadPath(),
                user.getId()
        );

        try {
            Files.createDirectories(newFolder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create folder for job: " + newFolder);
        }
    }
}
