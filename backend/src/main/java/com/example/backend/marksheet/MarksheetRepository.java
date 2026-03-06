package com.example.backend.marksheet;

import com.example.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarksheetRepository extends JpaRepository<Marksheet, UUID> {
    List<Marksheet> findAllByProject(Project project);
}
