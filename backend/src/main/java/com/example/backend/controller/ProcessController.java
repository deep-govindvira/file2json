package com.example.backend.controller;

import com.example.backend.props.AppProps;
import com.example.backend.service.MarksheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ProcessController {

    // Absolute path of the folder containing files
    private static final String FOLDER_PATH = "/Users/dhiraj/Desktop/deep/github/file2json/processing/debug_files";
    private static final String FASTAPI_URL = "http://localhost:8000/process"; // FastAPI URL
    private final RestTemplate restTemplate = new RestTemplate();
    private final MarksheetService marksheetService;
    private AppProps appProps;

    public ProcessController(MarksheetService marksheetService) {
        this.appProps = appProps;
        this.marksheetService = marksheetService;
    }

    @PostMapping("/process")
    public ResponseEntity<List<Map<String, Object>>> processFolder() {
        List<Map<String, Object>> results = new ArrayList<>();

        File folder = new File(FOLDER_PATH);
        if (!folder.exists() || !folder.isDirectory()) {
            return ResponseEntity.status(500)
                    .body(null);
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return ResponseEntity.ok(results);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        for (File file : files) {
            if (file.isFile()) {
                try {
                    String jsonRequest = "{\"file_path\":\"" + file.getAbsolutePath().replace("\\", "\\\\") + "\"}";
                    HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

                    var response = restTemplate.postForEntity(FASTAPI_URL, entity, Map.class);
                    marksheetService.save(response);


                } catch (Exception e) {
                    e.printStackTrace();
                    results.add(Map.of(
                            "file", file.getName(),
                            "error", "Failed to process"
                    ));
                }
            }
        }

        return ResponseEntity.ok(results);
    }
}
