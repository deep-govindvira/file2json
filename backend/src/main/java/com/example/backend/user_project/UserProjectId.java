package com.example.backend.user_project;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserProjectId implements Serializable {
    private UUID userId;
    private UUID projectId;
}
