package com.example.backend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserConverter converter;

    @Override
    public List<User> saveAll(List<User> userList) {
        return repository.saveAll(userList);
    }

    @Override
    public Optional<User> findById(String userId) {
        return repository.findById(userId);
    }

    @Override
    public RegisterUserResponse registerUser(RegisterUserRequest request) {
        User user = converter.user(request);
        User saved = repository.save(user);
        return converter.registerUserResponse(saved);
    }

    @Override
    public LoginUserResponse loginUser(LoginUserRequest request) {
        User user = converter.user(request);
        User existingUser = repository.findByEmail(user.getEmail()).orElseThrow();
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("User not found");
        }
        return converter.loginUserResponse(existingUser);
    }
}
