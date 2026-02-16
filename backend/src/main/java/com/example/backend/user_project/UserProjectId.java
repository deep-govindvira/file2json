package com.example.backend.user_project;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserProjectId implements Serializable {
    private String userId;
    private String projectId;
}
