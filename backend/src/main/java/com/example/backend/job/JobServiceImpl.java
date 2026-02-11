package com.example.backend.job;

import com.example.backend.config.AppProps;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository repository;
    private final UserService userService;
    private final AppProps props;

    @Override
    public ResponseEntity<CreateJobResponse> addJobToUser(String userId, CreateJobRequest request) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = JobConverter.toJob(request);
        job.setUser(user);
        Job savedJob = repository.save(job);
        createFolderForJob(savedJob);
        CreateJobResponse response = JobConverter.toCreateJobResponse(savedJob);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public Optional<Job> findById(String id) {
        return repository.findById(id);
    }

    private void createFolderForJob(Job job) {
        Path folderPath = Paths.get(
                props.getUploadPath(),
                job.getUser().getId(),
                job.getId()
        );

        try {
            Files.createDirectories(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create folder for job: " + folderPath);
        }
    }
}
