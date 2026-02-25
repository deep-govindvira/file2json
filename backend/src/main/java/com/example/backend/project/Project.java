package com.example.backend.project;

import com.example.backend.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marksheet_processing_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "project_name")
    private String name = "";

    @Column(name = "project_description")
    private String description = "";

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

//    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserProject> users = new ArrayList<>();
}
