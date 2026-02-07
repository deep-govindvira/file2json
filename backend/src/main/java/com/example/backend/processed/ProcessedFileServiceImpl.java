package com.example.backend.processed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProcessedFileServiceImpl implements ProcessedFileService {

    private final ProcessedFileRepository processedFileRepository;

    @Override
    public boolean isProcessed(File file) {
        return processedFileRepository.existsByFilePathAndProcessed(file.getAbsolutePath(), true);
    }

    @Override
    public ProcessedFile markAsProcessed(File file) {
        ProcessedFile processedFile = new ProcessedFile();
        processedFile.setFilePath(file.getAbsolutePath());
        processedFile.setProcessed(true);
        processedFile.setProcessedAt(LocalDateTime.now());
        return processedFileRepository.save(processedFile);
    }

    @Override
    public Optional<ProcessedFile> findByFilePath(String filePath) {
        return processedFileRepository.findByFilePath(filePath);
    }

    @Override
    public List<ProcessedFile> getUnprocessedFiles() {
        return processedFileRepository.findByProcessed(false);
    }

}
