package com.example.backend.job;

import com.example.backend.Status;
import com.example.backend.marksheet.Marksheet;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String folderPath;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UNPROCESSED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Marksheet> marksheets = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (folderPath == null) {
            folderPath = java.util.UUID.randomUUID().toString();
        }
    }
}
