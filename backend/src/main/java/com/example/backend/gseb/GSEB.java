package com.example.backend.gseb;

import com.example.backend.marksheet.MarksheetInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gseb_marksheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@DiscriminatorValue("GSEB")
public class GSEB extends MarksheetInfo {
    //    private String board;
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

    @OneToMany(mappedBy = "gseb", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<GSEBMark> gsebMarks = new ArrayList<>();
}
