package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gseb_marksheet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GsebMarksheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String board;
    private String monthYearExam;
    private String seatNo;
    private String centreNo;
    private String schoolIndexNo;
    private String groupName;
    private String studentName;

    private Integer totalMarks;
    private Integer obtainedMarks;
    private String obtainedInWords;

    private LocalDate examDate;
    private String result;

    @Column(columnDefinition = "TEXT")
    private String correctedNotes;

    @OneToMany(
            mappedBy = "marksheet",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<GsebSubject> subjects = new ArrayList<>();
}
