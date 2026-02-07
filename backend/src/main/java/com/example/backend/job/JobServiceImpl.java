package com.example.backend.job;

import com.example.backend.config.AppProps;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import lombok.RequiredArgsConstructor;
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
    public CreateJobResponse addJobToUser(String userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = Job.builder().user(user).build();
        Job savedJob = repository.save(job);
        createFolderForJob(job);
        return JobConverter.toCreateJobResponse(savedJob);
    }

    @Override
    public Optional<Job> findById(String id) {
        return repository.findById(id);
    }

    private void createFolderForJob(Job job) {
        Path folderPath = Paths.get(
                props.getUploadPath(),
                job.getUser().getFolderPath(),
                job.getFolderPath()
        );

        try {
            Files.createDirectories(folderPath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create folder for job: " + folderPath);
        }
    }
}
