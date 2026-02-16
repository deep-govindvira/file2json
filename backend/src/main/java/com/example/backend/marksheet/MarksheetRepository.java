package com.example.backend.marksheet;

import com.example.backend.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarksheetRepository extends JpaRepository<Marksheet, String> {
    List<Marksheet> findAllByProject(Project project);
}
