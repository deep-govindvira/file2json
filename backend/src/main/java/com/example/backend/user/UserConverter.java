package com.example.backend.user;

public class UserConverter {
    public static User toUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    public static CreateUserResponse toCreateUserResponse(User user) {
        return CreateUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .folderPath(user.getFolderPath())
                .build();
    }
}
