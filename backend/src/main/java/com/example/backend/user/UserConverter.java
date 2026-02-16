package com.example.backend.user;

import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public User user(RegisterUserRequest request) {
        return User.builder()
                .department(request.getDepartment())
                .name(request.getUserName())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
    }

    public RegisterUserResponse registerUserResponse(User user) {
        return RegisterUserResponse.builder()
                .userDepartment(user.getDepartment())
                .userEmail(user.getEmail())
                .userId(user.getId())
                .userName(user.getName())
                .build();
    }

    public User user(LoginUserRequest request) {
        return User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public LoginUserResponse loginUserResponse(User user) {
        return LoginUserResponse.builder()
                .userId(user.getId())
                .build();
    }
}
