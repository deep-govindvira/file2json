package com.example.backend.processed;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processed_files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String filePath;

    private boolean processed; // true if processed successfully

    private LocalDateTime processedAt;
}
