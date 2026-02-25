package com.example.backend.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository repository;

    public void addBoards(List<Board> boards) {
        for (Board board : boards) {
            try {
                repository.save(board);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Optional<Board> findByShortName(String name) {
        return Optional.ofNullable(repository.findByShortName(name));
    }
}
