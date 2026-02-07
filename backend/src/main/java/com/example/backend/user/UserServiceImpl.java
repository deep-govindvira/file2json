package com.example.backend.user;

import com.example.backend.config.AppProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
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
        repository.save(user);
        createFolderForUser(user);
        return UserConverter.toCreateUserResponse(user);
    }

    private void createFolderForUser(User user) {
        File newFolder = new File(props.getUploadPath(), user.getFolderPath());
        if (!newFolder.exists()) {
            if (!newFolder.mkdirs()) {
                throw new RuntimeException("Failed to create folder for user: " + newFolder.getAbsolutePath());
            }
        } else throw new RuntimeException("Failed to create folder for user: " + newFolder.getAbsolutePath());
    }
}
