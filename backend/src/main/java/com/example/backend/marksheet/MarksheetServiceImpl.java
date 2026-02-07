package com.example.backend.marksheet;

import com.example.backend.cbse.marksheet.CbseMarksheet;
import com.example.backend.cbse.marksheet.CbseMarksheetConverter;
import com.example.backend.cbse.marksheet.CbseMarksheetResponse;
import com.example.backend.cbse.marksheet.CbseMarksheetService;
import com.example.backend.cbse.subject.CbseSubject;
import com.example.backend.cbse.subject.CbseSubjectConverter;
import com.example.backend.config.AppProps;
import com.example.backend.gseb.marksheet.GsebMarksheet;
import com.example.backend.gseb.marksheet.GsebMarksheetConverter;
import com.example.backend.gseb.marksheet.GsebMarksheetResponse;
import com.example.backend.gseb.marksheet.GsebMarksheetService;
import com.example.backend.gseb.subject.GsebSubject;
import com.example.backend.gseb.subject.GsebSubjectConverter;
import com.example.backend.job.Job;
import com.example.backend.job.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

    private static final String FOLDER_PATH = "/Users/dhiraj/Desktop/deep/github/file2json/processing/debug_files";

    private final GsebMarksheetService gsebMarksheetService;
    private final CbseMarksheetService cbseMarksheetService;
    private final MarksheetRepository repository;
    private final JobService jobService;
    private final AppProps props;
    private final ExecutorService executorService;

    ObjectMapper objectMapper = new ObjectMapper();

    private void saveFile(Marksheet marksheet, MultipartFile file) {
        String folderPath = Paths.get(props.getUploadPath(),
                marksheet.getJob().getUser().getFolderPath(),
                marksheet.getJob().getFolderPath()).toString();

        File folder = new File(folderPath);
        String originalFileName = file.getOriginalFilename();
        File targetFile = new File(folder, originalFileName);

        int counter = 1;
        String name = FilenameUtils.getBaseName(originalFileName); // without extension
        String extension = FilenameUtils.getExtension(originalFileName); // extension
        while (targetFile.exists()) {
            String newFileName = name + "-" + counter + "." + extension;
            targetFile = new File(folder, newFileName);
            counter++;
        }

        try {
            marksheet.setFilePath(targetFile.getName());
            file.transferTo(targetFile);
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
        saveFile(marksheet, file);
        return MarksheetConverter.toCreateMarksheetResponse(savedMarksheet);
    }

    @Override
    public void save(ResponseEntity<Map> response) {
        switch (response.getBody().get("board").toString()) {
            case "GSEB":
                GsebMarksheetResponse gsebMarksheetResponse = objectMapper.convertValue(response.getBody(), GsebMarksheetResponse.class);
                GsebMarksheet gsebMarksheet = GsebMarksheetConverter.toGsebMarksheet(gsebMarksheetResponse);

                gsebMarksheetResponse.getSubjects().forEach(s -> {
                    GsebSubject gsebSubject = GsebSubjectConverter.toGsebSubject(s);
                    gsebSubject.setMarksheet(gsebMarksheet);
                    gsebMarksheet.getSubjects().add(gsebSubject);
                });

                gsebMarksheetService.save(gsebMarksheet);

                log.info("GsebMarksheetResponse: {}", gsebMarksheetResponse);
                break;

            case "CBSE":
                CbseMarksheetResponse cbseMarksheetResponse = objectMapper.convertValue(response.getBody(), CbseMarksheetResponse.class);
                CbseMarksheet cbseMarksheet = CbseMarksheetConverter.toCbseMarksheet(cbseMarksheetResponse);

                cbseMarksheetResponse.getSubjects().forEach(s -> {
                    CbseSubject subject = CbseSubjectConverter.toCbseSubject(s);
                    cbseMarksheet.getSubjects().add(subject);
                });

                cbseMarksheetService.save(cbseMarksheet);

                log.info("GsebMarksheetResponse: {}", cbseMarksheetResponse);
                break;
        }
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
}
