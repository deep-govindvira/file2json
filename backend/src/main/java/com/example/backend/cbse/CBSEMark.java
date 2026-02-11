package com.example.backend.cbse;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cbse_marks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CBSEMark {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mark_id")
    private String id;

    private String subCode;
    private String subject;
    private Integer theory;
    private Integer practical;
    private Integer total;
    private String totalInWords;
    private String positionalGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_info_id", nullable = false)
    @ToString.Exclude
    private CBSE cbse;
}
