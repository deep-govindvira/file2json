package com.example.backend.gseb.marksheet;

import com.example.backend.gseb.subject.GsebSubject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gseb_marksheet")
@Data
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
