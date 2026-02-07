package com.example.backend.marksheet;

import com.example.backend.Status;
import com.example.backend.job.Job;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String filePath;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Board board = Board.UNKNOWN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.UNPROCESSED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

//    @OneToOne(mappedBy = "marksheet", cascade = CascadeType.ALL)
//    private GsebMarksheet gseb;
//
//    @OneToOne(mappedBy = "marksheet", cascade = CascadeType.ALL)
//    private CbseMarksheet cbse;
//
//    @OneToOne(mappedBy = "marksheet", cascade = CascadeType.ALL)
//    private IcseMarksheet icse;
}

