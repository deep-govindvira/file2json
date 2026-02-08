package com.example.backend.marksheet;

import com.example.backend.Status;
import com.example.backend.config.AppProps;
import com.example.backend.gseb.GSEBConverter;
import com.example.backend.gseb.StructuredGSEBResponse;
import com.example.backend.job.Job;
import com.example.backend.job.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarksheetServiceImpl implements MarksheetService {

    private final MarksheetRepository repository;

    private final JobService jobService;
    private final ExecutorService executorService;

    private final AppProps props;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    private void saveFile(Marksheet marksheet, MultipartFile file) {
        String folderPath = Paths.get(
                props.getUploadPath(),
                marksheet.getJob().getUser().getId(),
                marksheet.getJob().getId()
        ).toString();

        File folder = new File(folderPath);

        String originalFileName = file.getOriginalFilename();

//        String name = originalFileName;
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            int dotIndex = originalFileName.lastIndexOf(".");
//            name = originalFileName.substring(0, dotIndex);   // without extension
            extension = originalFileName.substring(dotIndex); // .pdf
        }

//        String newFileName = marksheet.getId() + "_" + name + extension;
        String newFileName = marksheet.getId() + extension;
//        String newFileName = marksheet.getId();

        File targetFile = new File(folder, newFileName);

        try {
            file.transferTo(targetFile);
            marksheet.setName(originalFileName);
            repository.save(marksheet);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    private SaveMarksheetResponse saveMarksheet(String userId, String jobId, MultipartFile file) {
        Job job = jobService.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job Not found with id: " + jobId));
        Marksheet marksheet = Marksheet.builder()
                .job(job)
                .build();
        Marksheet savedMarksheet = repository.save(marksheet);
        saveFile(savedMarksheet, file);
        return MarksheetConverter.toCreateMarksheetResponse(savedMarksheet);
    }

    @Override
    public List<SaveMarksheetResponse> saveMarksheets(String userId, String jobId, List<MultipartFile> files) {
        List<SaveMarksheetResponse> list = new ArrayList<>();
        files.forEach(file -> {
            SaveMarksheetResponse response = saveMarksheet(userId, jobId, file);
            list.add(response);
        });
        return list;
    }

    @Override
    public List<ProcessMarksheetResponse> processMarksheets(String userId, String jobId) {
        String folderPath = Paths.get(
                props.getUploadPath(),
                userId,
                jobId
        ).toString();

        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            log.error("Folder does not exist or is not a directory");
            return new ArrayList<>();
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log.info("No files to process");
            return new ArrayList<>();
        }

        List<ProcessMarksheetResponse> processMarksheetResponseList = new ArrayList<>();

        for (File file : files) {
            processMarksheet(file, processMarksheetResponseList);
        }
        return processMarksheetResponseList;
    }

    private synchronized void processMarksheet(File file,
                                               List<ProcessMarksheetResponse> processMarksheetResponseList) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        String nameWithoutExtension = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);

        Marksheet marksheet = repository.findById(nameWithoutExtension)
                .orElseThrow(() -> new RuntimeException("Marksheet not found for file: " + fileName));


        if (marksheet.getStatus().equals(Status.UNPROCESSED)) {
            marksheet.setStatus(Status.PROCESSING);
            repository.save(marksheet);

            executorService.submit(() ->
                    processMarksheetInBackground(file, marksheet, processMarksheetResponseList)
            );
        }

        ProcessMarksheetResponse processMarksheetResponse =
                MarksheetConverter.toProcessMarksheetResponse(marksheet);
        processMarksheetResponseList.add(processMarksheetResponse);
    }

    private void processMarksheetInBackground(File file, Marksheet marksheet,
                                              List<ProcessMarksheetResponse> processMarksheetResponseList) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonRequest = "{\"file_path\":\"" +
                file.getAbsolutePath().replace("\\", "\\\\") + "\"}";

        HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

        ResponseEntity<Map> response = template.postForEntity(props.getProcessApiUrl(), entity, Map.class);

        saveStructuredResponse(response, marksheet, processMarksheetResponseList);
    }

    private void saveStructuredResponse(
            ResponseEntity<Map> response,
            Marksheet marksheet,
            List<ProcessMarksheetResponse> processMarksheetResponseList) {
        switch (response.getBody().get("board").toString()) {
            case "GSEB":
                StructuredGSEBResponse structuredGSEBResponse = mapper.convertValue(
                        response.getBody(), StructuredGSEBResponse.class);
                Board board = GSEBConverter.toGSEB(structuredGSEBResponse);
                marksheet.setBoard(board);
                marksheet.setStatus(Status.COMPLETED);
                repository.save(marksheet);
                break;
        }
    }
}
