package com.example.backend.user;

import lombok.Data;

@Data
public class LoginUserRequest {
    private String email;
    private String password;
}
