package com.example.backend.auth.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String token;
}
