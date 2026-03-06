package com.example.backend.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Modifying
    @Query("""
                UPDATE Project p SET
            
                    p.totalMarksheets = (
                        SELECT COUNT(m) FROM Marksheet m
                        WHERE m.project.id = :projectId
                    ),
            
                    p.processedMarksheets = (
                        SELECT COUNT(m) FROM Marksheet m
                        WHERE m.project.id = :projectId
                        AND m.processingStatus = 'COMPLETED'
                    ),
            
                    p.processingFailedMarksheets = (
                        SELECT COUNT(m) FROM Marksheet m
                        WHERE m.project.id = :projectId
                        AND m.processingStatus = 'FAILED'
                    ),
            
                    p.processingDuration = (
                        SELECT
                            CASE
                                WHEN COUNT(m) < :threads THEN COALESCE(MAX(m.processingDuration), 0)
                                ELSE COALESCE(SUM(m.processingDuration), 0) / :threads
                            END
                        FROM Marksheet m
                        WHERE m.project.id = :projectId
                    ),
            
                    p.status = CASE
            
                        WHEN (
                            SELECT COUNT(m) FROM Marksheet m
                            WHERE m.project.id = :projectId
                            AND m.processingStatus <> 'UNPROCESSED'
                        ) = 0
                        THEN com.example.backend.project.ProjectStatus.UNPROCESSED
            
                        WHEN (
                            (SELECT COUNT(m) FROM Marksheet m
                             WHERE m.project.id = :projectId
                             AND m.processingStatus IN ('UNPROCESSED', 'FAILED')
                            ) = 0
                        )
                        AND (
                            (SELECT COUNT(m) FROM Marksheet m
                             WHERE m.project.id = :projectId
                             AND m.processingStatus IN ('QUEUED', 'PROCESSING')
                            ) > 0
                        )
                        THEN com.example.backend.project.ProjectStatus.PROCESSING
            
                        WHEN (
                            SELECT COUNT(m) FROM Marksheet m
                            WHERE m.project.id = :projectId
                            AND m.processingStatus IN ('COMPLETED','FAILED')
                        ) = (
                            SELECT COUNT(m) FROM Marksheet m
                            WHERE m.project.id = :projectId
                        )
                        THEN com.example.backend.project.ProjectStatus.COMPLETED
            
                        ELSE com.example.backend.project.ProjectStatus.PARTIALLY_COMPLETED
            
                    END
            
                WHERE p.id = :projectId
            """)
    void refreshProjectStatistics(@Param("projectId") UUID projectId,
                                  @Param("threads") Long threads);
}