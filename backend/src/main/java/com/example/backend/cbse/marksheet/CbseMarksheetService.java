package com.example.backend.cbse.marksheet;

import java.util.List;
import java.util.Optional;

public interface CbseMarksheetService {
    CbseMarksheet save(CbseMarksheet marksheet);

    Optional<CbseMarksheet> findById(Long id);

    List<CbseMarksheet> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}

