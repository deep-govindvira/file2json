package com.example.backend.gseb;

import com.example.backend.marksheet.Board;
import com.example.backend.marksheet.Marksheet;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gseb")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("GSEB")
public class GSEB extends Board {
    private String board;
    private String monthAndYearOfExam;
    private String seatNo;
    private String centreNo;
    private String schoolIndexNo;
    private String groupName;
    private String studentName;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
    private String result;
    private String corrected;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "marksheet_id", unique = true)
    @ToString.Exclude
    private Marksheet marksheet;

    @OneToMany(mappedBy = "gseb", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<GSEBSubject> gsebSubjects = new ArrayList<>();
}
