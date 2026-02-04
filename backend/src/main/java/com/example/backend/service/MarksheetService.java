package com.example.backend.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface MarksheetService {
    void save(ResponseEntity<Map> response);
}
