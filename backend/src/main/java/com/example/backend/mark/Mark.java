package com.example.backend.mark;

import com.example.backend.Audit;
import com.example.backend.marksheet.Marksheet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marksheet_marks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mark extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    // Foreign key to marksheet
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "student_marksheets_id")
    private Marksheet marksheet;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "subject_grade")
    private String subjectGrade;

    @Column(name = "subject_out_of_marks")
    private Integer subjectOutOfMarks;

    @Column(name = "subject_obtained_marks")
    private Integer obtained;

    @Column(name = "subject_obtained_marks_in_words")
    private String obtainedInWords;
}
