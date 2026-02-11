package com.example.backend.icse;

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
@Table(name = "icse_marksheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
//@DiscriminatorValue("ICSE")
public class ICSE extends MarksheetInfo {
    //    private String board;
    private String school;

    private Long uniqueId;
    private String motherName;
    private String fatherName;


    @OneToMany(mappedBy = "icse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<ICSEMark> icseMarks = new ArrayList<>();

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    private String result;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private String corrected;
}
