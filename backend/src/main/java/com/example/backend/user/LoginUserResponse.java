package com.example.backend.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginUserResponse {
    private String userId;
    private String userName;
    private String userEmail;
    private String userDepartment;
    private String role;
}
