package com.example.backend.gseb;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gseb_marks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GSEBMark {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mark_id")
    private String id;

    private String subCode;
    private String subject;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_info_id", nullable = false)
    @ToString.Exclude
    private GSEB gseb;
}
