package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, String> {
    List<Mark> findByMarksheet(Marksheet marksheet);
}
