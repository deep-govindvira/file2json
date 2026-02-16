package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByName(String name);

    Optional<User> findByEmail(String email);
}
