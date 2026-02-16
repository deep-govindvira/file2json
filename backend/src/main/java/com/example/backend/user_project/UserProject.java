package com.example.backend.user_project;

import com.example.backend.Audit;
import com.example.backend.project.Project;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProject extends Audit {
    @EmbeddedId
    @Builder.Default
    private UserProjectId id = new UserProjectId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "marksheet_processing_projects_id")
    private Project project;
}

