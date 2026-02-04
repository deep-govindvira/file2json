package com.example.backend.service;

import com.example.backend.entity.GsebMarksheet;

import java.util.List;
import java.util.Optional;

public interface GsebMarksheetService {

    GsebMarksheet save(GsebMarksheet marksheet);

    Optional<GsebMarksheet> findById(Long id);

    List<GsebMarksheet> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
