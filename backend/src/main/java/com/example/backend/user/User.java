package com.example.backend.user;

import com.example.backend.job.Job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String folderPath;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password; // hashed password

    @Column(unique = true, nullable = false)
    private String email;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (folderPath == null) {
            folderPath = java.util.UUID.randomUUID().toString();
        }
    }
}
