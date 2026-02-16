package com.example.backend.marksheet_summary;

import com.example.backend.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marksheet_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarksheetSummary extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "year_of_passing")
    private Integer yearOfPassing;

    @Column(name = "total_out_of_marks")
    private Integer totalOutOfMarks;

    @Column(name = "total_obtained_marks")
    private Integer totalObtainedMarks;

    @Column(name = "obtained_percentage")
    private Double obtainedPercentage;

    @Column(name = "obtained_percentile")
    private Double obtainedPercentile;

    @Column(name = "obtained_grade")
    private String obtainedGrade;

    @Column(name = "result_status")
    private String resultStatus;
}
