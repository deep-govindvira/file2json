package com.example.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String role;
}
