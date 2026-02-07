package com.example.backend.marksheet;

import com.example.backend.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveMarksheetResponse {
    private String id;
    private String filePath;
    private Status status;
}