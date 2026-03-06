package com.example.backend.user_project;

import com.example.backend.project.Project;
import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserProjectRepository extends JpaRepository<UserProject, UserProjectId> {
    // Get all projects for a given userId
    @Query("SELECT up.project FROM UserProject up WHERE up.user.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") UUID userId);

    // Get all users for a given projectId
    @Query("SELECT up.user FROM UserProject up WHERE up.project.id = :projectId")
    List<User> findUsersByProjectId(@Param("projectId") UUID projectId);

}
