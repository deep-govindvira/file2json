package com.example.backend.project;

import com.example.backend.Audit;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "marksheet_processing_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "project_name")
    private String name = "";

    @Column(name = "project_description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStatus status = ProjectStatus.UNPROCESSED;

//    @Column(name = "project_status_updated_at")
//    private LocalDateTime statusUpdatedAt = LocalDateTime.now();

    @Column(name = "project_processing_duration")
    private Long processingDuration;

    @Column(name = "year")
    private Integer year = java.time.Year.now().getValue();

    @Column(name = "processed_marksheets")
    private Integer processedMarksheets = 0;

    @Column(name = "processing_failed_marksheets")
    private Integer processingFailedMarksheets = 0;

    @Column(name = "total_marksheets")
    private Integer totalMarksheets = 0;

    @ManyToOne
    @JoinColumn(name = "projectCreator", nullable = false)
    private User projectCreator;

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserProject> users = new ArrayList<>();
}
