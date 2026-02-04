package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "cbse_marksheets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbseMarksheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String board;

    private String studentsName;

    private Integer rollNo;

    private String mothersName;

    private String fatherName;

    private String school;

    private String result;

    private String date;

    @Column(length = 1000)
    private String corrected;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "marksheet_id") // Foreign key in Subject table
    private List<CbseSubject> subjects;
}
