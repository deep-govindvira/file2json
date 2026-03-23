package com.example.backend.marksheet;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ExportRequest {
    private List<String> columns;
}