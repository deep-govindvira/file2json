package com.example.backend.marksheet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "board_type")
public abstract class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
}
