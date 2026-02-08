package com.example.backend.marksheet;

import com.example.backend.Status;
import com.example.backend.job.Job;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marksheets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marksheet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id", referencedColumnName = "id", unique = true)
    private Board board;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UNPROCESSED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    @ToString.Exclude
    private Job job;

//    @OneToOne(mappedBy = "gseb", cascade = CascadeType.ALL)
//    @ToString.Exclude
//    private GSEB gseb;

//
//    @OneToOne(mappedBy = "marksheet", cascade = CascadeType.ALL)
//    private CbseMarksheet cbse;
//
//    @OneToOne(mappedBy = "marksheet", cascade = CascadeType.ALL)
//    private IcseMarksheet icse;
}

