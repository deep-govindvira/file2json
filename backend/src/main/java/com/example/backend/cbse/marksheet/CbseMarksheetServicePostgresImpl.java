package com.example.backend.cbse.marksheet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CbseMarksheetServicePostgresImpl implements CbseMarksheetService {

    private final CbseMarksheetRepository cbseMarksheetRepository;

    @Override
    public CbseMarksheet save(CbseMarksheet marksheet) {
        return cbseMarksheetRepository.save(marksheet);
    }

    @Override
    public Optional<CbseMarksheet> findById(Long id) {
        return cbseMarksheetRepository.findById(id);
    }

    @Override
    public List<CbseMarksheet> findAll() {
        return cbseMarksheetRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        cbseMarksheetRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return cbseMarksheetRepository.existsById(id);
    }
}
