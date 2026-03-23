package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import com.example.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MarkRepository extends JpaRepository<Mark, UUID> {
    List<Mark> findByMarksheet(Marksheet marksheet);

    void deleteByMarksheet(Marksheet marksheet);

    @Query("""
                SELECT DISTINCT m.subjectName
                FROM Mark m
                ORDER BY m.subjectName
            """)
    List<String> findAllDistinctSubjectNames();

    @Query("SELECT m FROM Mark m WHERE m.marksheet.project = :project")
    List<Mark> findAllByProject(@Param("project") Project project);

}