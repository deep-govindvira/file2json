package com.example.backend.user;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(String id);

    CreateUserResponse createUser(CreateUserRequest request);
}
