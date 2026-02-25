package com.example.backend.marksheet;

import com.example.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarksheetRepository extends JpaRepository<Marksheet, String> {
    List<Marksheet> findAllByProject(Project project);

    // ==============================
    // TOTAL PROCESSED (COMPLETED)
    // ==============================
    long countByProjectAndProcessingStatus(Project project,
                                           ProcessingStatus processingStatus);
    // ==============================
    // OPTIONAL: Specific Methods
    // ==============================

    default int countProcessed(Project project) {
        return (int) countByProjectAndProcessingStatus(project, ProcessingStatus.COMPLETED);
    }

    default int countFailed(Project project) {
        return (int) countByProjectAndProcessingStatus(project, ProcessingStatus.FAILED);
    }

    default long countTotal(Project project) {
        return countByProject(project);
    }

    long countByProject(Project project);

}
