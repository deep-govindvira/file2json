package com.example.backend.board;

import com.example.backend.Audit;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "boards")
@Data
@Builder
public class Board extends Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "board_full_name", unique = true)
    private String fullName;

    @Column(name = "board_short_name", length = 50)
    private String shortName;

    @Column(name = "board_state")
    private String state;

    @Column(name = "board_city")
    private String city;
}
