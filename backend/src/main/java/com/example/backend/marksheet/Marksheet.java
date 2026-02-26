package com.example.backend.marksheet;

import com.example.backend.Audit;
import com.example.backend.board.Board;
import com.example.backend.marksheet_summary.MarksheetSummary;
import com.example.backend.project.Project;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_marksheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marksheet extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "url")
    private String url;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status")
    private ProcessingStatus processingStatus = ProcessingStatus.UNPROCESSED;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_duration")
    private Long processingDuration;

    @Column(name = "seat_no")
    private String seatNo;

    @Column(name = "school_centre_no")
    private String schoolCentreNo;

    @Column(name = "school_index_no")
    private String schoolIndexNo;

    @Column(name = "group_name")
    private String group;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "year")
    private Long year;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "exam_boards_id")
    private Board board;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "marksheet_summary_id", referencedColumnName = "id")
    private MarksheetSummary marksheetSummary;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "verified_by_users_id", referencedColumnName = "id")
    private User verifiedByUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "marksheet_processing_projects_id")
    private Project project;

    @Column(name = "corrected", columnDefinition = "TEXT")
    private String corrected;
}
