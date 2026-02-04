package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gseb_subject_mark")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GsebSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subCode;
    private String subjectName;
    private Integer totalMarks;
    private Integer obtainedMarks;
    private String obtainedInWords;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_id")
    private GsebMarksheet marksheet;
}
