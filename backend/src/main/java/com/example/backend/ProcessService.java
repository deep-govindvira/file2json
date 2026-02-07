package com.example.backend;

import com.example.backend.marksheet.MarksheetService;
import com.example.backend.processed.ProcessedFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessService {
    private static final String FOLDER_PATH = "/Users/dhiraj/Desktop/deep/github/file2json/processing/debug_files";
    private static final String FASTAPI_URL = "http://localhost:8000/process";

    private final RestTemplate restTemplate;
    private final MarksheetService marksheetService;
    private final ProcessedFileService processedFileService;
    private final ExecutorService executor;

    public void process() {
        try {
            File folder = new File(FOLDER_PATH);
            if (!folder.exists() || !folder.isDirectory()) {
                log.error("Folder does not exist or is not a directory");
                return;
            }

            File[] files = folder.listFiles();
            if (files == null || files.length == 0) {
                log.info("No files to process");
                return;
            }


            for (File file : files) {
                processSingleFile(file);
            }

            log.info("All files processed successfully");

        } catch (Exception e) {
            log.info("Some of files are not processed");
        }
    }

    private synchronized void processSingleFile(File file) {
        // already done → skip
        if (processedFileService.isProcessed(file)) {
            log.info("File already processed: {}", file.getName());
            return;
        }

        // ✅ mark FIRST to avoid duplicate submissions
        processedFileService.markAsProcessed(file);

        executor.submit(() -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String jsonRequest =
                        "{\"file_path\":\"" +
                                file.getAbsolutePath().replace("\\", "\\\\") + "\"}";

                HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

                ResponseEntity<Map> response =
                        restTemplate.postForEntity(FASTAPI_URL, entity, Map.class);

                marksheetService.save(response);

                log.info("Processed file: {}", file.getName());

            } catch (Exception e) {
                log.error("Failed to process file: {}", file.getName(), e);

//                processedFileService.unmark(file);
            }
        });
    }
}
