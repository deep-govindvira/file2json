package com.example.backend.service;

import com.example.backend.entity.GsebMarksheet;
import com.example.backend.repository.GsebMarksheetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GsebMarksheetServicePostgresImpl implements GsebMarksheetService {

    private final GsebMarksheetRepository gsebMarksheetRepository;

    public GsebMarksheetServicePostgresImpl(GsebMarksheetRepository gsebMarksheetRepository) {
        this.gsebMarksheetRepository = gsebMarksheetRepository;
    }

    @Override
    public GsebMarksheet save(GsebMarksheet marksheet) {
        return gsebMarksheetRepository.save(marksheet);
    }

    @Override
    public Optional<GsebMarksheet> findById(Long id) {
        return gsebMarksheetRepository.findById(id);
    }

    @Override
    public List<GsebMarksheet> findAll() {
        return gsebMarksheetRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        gsebMarksheetRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return gsebMarksheetRepository.existsById(id);
    }
}
