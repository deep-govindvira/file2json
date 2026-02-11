package com.example.backend.marksheet;

import com.example.backend.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveMarksheetResponse {
    private String id;
    private String name;
    private Status status;
}