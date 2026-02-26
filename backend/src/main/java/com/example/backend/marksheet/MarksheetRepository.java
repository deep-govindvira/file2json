package com.example.backend.marksheet;

import com.example.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    default int countTotal(Project project) {
        return (int) countByProject(project);
    }

    long countByProject(Project project);

    @Query("SELECT COALESCE(SUM(m.processingDuration), 0) " +
            "FROM Marksheet m WHERE m.project.id = :projectId")
    Long sumProcessingDurationByProjectId(@Param("projectId") String projectId);

}
