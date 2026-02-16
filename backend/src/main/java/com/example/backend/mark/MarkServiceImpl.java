package com.example.backend.mark;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkServiceImpl implements MarkService {

    private final MarkRepository repository;

    @Override
    public List<Mark> saveAll(List<Mark> markList) {
        return repository.saveAll(markList);
    }
}
