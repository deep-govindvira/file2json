package com.example.backend.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserResponse {
    private String userId;
    private String name;
    private String email;
    private String department;
}
