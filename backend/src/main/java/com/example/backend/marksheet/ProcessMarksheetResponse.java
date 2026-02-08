package com.example.backend.marksheet;

import com.example.backend.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessMarksheetResponse {
    private String id;
    private String name;
    private Status status;
}
