package com.example.backend.user;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    List<User> saveAll(List<User> userList);

    Optional<User> findById(String userId);

    RegisterUserResponse registerUser(RegisterUserRequest request);

    String updateProfile(UpdateUserRequest request);

    RegisterUserResponse registerAdmin(RegisterAdminRequest request);

    List<GetUserResponse> getAllAdmins();

    LoginUserResponse loginUser(LoginUserRequest request);

    GetUserResponse getUserInfo();

    List<GetUserResponse> getUsersForProject(String projectId);

    Optional<User> findByEmail(String email);

    void deleteAdmin(String userId);
}
