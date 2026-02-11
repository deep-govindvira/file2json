package com.example.backend.cbse;

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
@Table(name = "cbse_marksheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@DiscriminatorValue("CBSE")
public class CBSE extends MarksheetInfo {
    //    private String board;
    private String studentName;
    private Integer rollNo;
    private String motherName;
    private String fatherName;
    private String school;
    private String result;
    private String corrected;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @OneToMany(mappedBy = "cbse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<CBSEMark> cbseMarks = new ArrayList<>();
}
