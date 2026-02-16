package com.example.backend.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> saveAll(List<User> userList);

    Optional<User> findById(String userId);

    RegisterUserResponse registerUser(RegisterUserRequest request);

    LoginUserResponse loginUser(LoginUserRequest request);
}
