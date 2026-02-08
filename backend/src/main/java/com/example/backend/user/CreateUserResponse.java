package com.example.backend.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    private String id;
    private String name;
    private String email;
}

