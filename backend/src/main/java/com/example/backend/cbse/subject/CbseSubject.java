package com.example.backend.cbse.subject;

import com.example.backend.cbse.marksheet.CbseMarksheet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cbse_subjects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CbseSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subCode;

    private String subject;

    private Integer theory;

    private Integer practical;

    private Integer total;

    private String totalInWords;

    private String positionalGrade;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marksheet_id")
    private CbseMarksheet marksheet;
}
