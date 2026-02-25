package com.example.backend;

import lombok.Data;

@Data
public class Correction {
    private String fieldName;
    private Object ocrValue;
    private Object correctedValue;
}