package com.example.backend.processed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProcessedFileRepository extends JpaRepository<ProcessedFile, Long> {
    Optional<ProcessedFile> findByFilePath(String filePath);

    boolean existsByFilePathAndProcessed(String filePath, boolean processed);

    List<ProcessedFile> findByProcessed(boolean b);
}

