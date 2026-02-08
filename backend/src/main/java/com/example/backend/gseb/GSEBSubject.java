package com.example.backend.gseb;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gseb_subject")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GSEBSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String subCode;
    private String subject;
    private Integer total;
    private Integer obtained;
    private String obtainedInWords;
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gseb_id", nullable = false)
    @ToString.Exclude
    private GSEB gseb;
}
