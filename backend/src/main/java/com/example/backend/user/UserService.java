package com.example.backend.user;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface UserService {
    Optional<User> findById(String id);

    ResponseEntity<CreateUserResponse> createUser(CreateUserRequest request);
}
