package com.example.backend;

import com.example.backend.board.Board;
import com.example.backend.board.BoardService;
import com.example.backend.config.AppProps;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AppProps props;
    private final BoardService boardService;

    @Override
    public void run(String... args) {
        addBoards();
    }

    void addBoards() {
        List<Board> boardList = new ArrayList<>();
        addGsebBoard(boardList);
        addCbseBoard(boardList);
        addIcseBoard(boardList);
        boardService.addBoards(boardList);
    }

    void addGsebBoard(List<Board> boardList) {
        Board gseb = Board.builder()
                .fullName("Gujarat Secondary and Higher Secondary Education Board")
                .city("Gandhinagar")
                .state("Gujarat")
                .shortName("GSHSEB")
                .build();
        boardList.add(gseb);
    }

    void addCbseBoard(List<Board> boardList) {
        Board board = Board.builder()
                .fullName("Central Board of Secondary Education")
                .city("New Delhi")
                .state("New Delhi")
                .shortName("CBSE")
                .build();
        boardList.add(board);
    }

    void addIcseBoard(List<Board> boardList) {
        Board board = Board.builder()
                .fullName("Indian Certificate of Secondary Education")
                .city("New Delhi")
                .state("New Delhi")
                .shortName("ICSE")
                .build();
        boardList.add(board);
    }
}

