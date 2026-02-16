package com.example.backend.board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {
    Board findByShortName(String name);
}
