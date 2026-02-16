package com.example.backend.board;

import java.util.List;
import java.util.Optional;

public interface BoardService {
    void addBoards(List<Board> boards);

    Optional<Board> findByShortName(String name);
}
