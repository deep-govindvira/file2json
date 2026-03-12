package com.example.backend.user;

import com.example.backend.department.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final DepartmentService departmentService;

    public User user(RegisterUserRequest request) {

        return User.builder()
                .department(departmentService.getDepartmentById(UUID.fromString(request.getDepartment())))
                .name(request.getUserName())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
    }

    public RegisterUserResponse registerUserResponse(User user) {
        return RegisterUserResponse.builder()
                .userDepartment(user.getDepartment().getName())
                .userEmail(user.getEmail())
                .userId(user.getId().toString())
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
                .userId(user.getId().toString())
                .build();
    }

    public GetUserResponse getUserResponse(User user) {
        return GetUserResponse.builder()
                .department(user.getDepartment().getName())
                .email(user.getEmail())
                .name(user.getName())
                .userId(user.getId().toString())
                .build();
    }
}
