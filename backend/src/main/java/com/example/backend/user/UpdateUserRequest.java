package com.example.backend.user;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String name;
    private String password;

}