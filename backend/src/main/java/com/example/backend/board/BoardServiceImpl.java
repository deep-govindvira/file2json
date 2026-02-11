package com.example.backend.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository repository;

    @Override
    public void addBoards(List<Board> boards) {
        for (Board board : boards) {
            try {
                repository.save(board);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
