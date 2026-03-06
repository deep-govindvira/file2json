package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MarkRepository extends JpaRepository<Mark, UUID> {
    List<Mark> findByMarksheet(Marksheet marksheet);

    void deleteByMarksheet(Marksheet marksheet);
}