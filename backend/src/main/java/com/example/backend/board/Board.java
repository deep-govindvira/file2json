package com.example.backend.board;

import com.example.backend.Audit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "exam_boards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "board_full_name")
    private String fullName;

    @Column(name = "board_short_name", length = 50)
    private String shortName;

    @Column(name = "board_state")
    private String state;

    @Column(name = "board_city")
    private String city;
}
