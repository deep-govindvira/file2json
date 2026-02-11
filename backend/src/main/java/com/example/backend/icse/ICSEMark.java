package com.example.backend.icse;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "icse_marks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ICSEMark {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mark_id")
    private String id;

    private String subject;
    private int percentageMarks;
    private String percentageMarksInWords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_info_id", nullable = false)
    @ToString.Exclude
    private ICSE icse;
}

