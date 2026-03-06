package com.example.backend.marksheet;

import com.example.backend.auth.service.AuthService;
import com.example.backend.board.Board;
import com.example.backend.board.BoardService;
import com.example.backend.cbse.CbseMarkResponse;
import com.example.backend.cbse.CbseResponse;
import com.example.backend.config.AppProps;
import com.example.backend.gseb.GsebMarkResponse;
import com.example.backend.gseb.GsebResponse;
import com.example.backend.icse.IcseMarkResponse;
import com.example.backend.icse.IcseResponse;
import com.example.backend.mark.GetMarkResponse;
import com.example.backend.mark.Mark;
import com.example.backend.mark.MarkService;
import com.example.backend.marksheet_summary.MarksheetSummary;
import com.example.backend.project.Project;
import com.example.backend.project.ProjectService;
import com.example.backend.sse.SseService;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProject;
import com.example.backend.user_project.UserProjectId;
import com.example.backend.user_project.UserProjectService;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarksheetService {

    private final SseService sseService;
    private final AppProps props;
    private final ProjectService projectService;
    private final MarksheetRepository repository;
    private final MarksheetConverter converter;
    private final UserProjectService userProjectService;
    private final ExecutorService executorService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final MarkService markService;
    private final BoardService boardService;
    private final AuthService authService;
    private final UserService userService;

    public void updateMarksheet(UUID projectId, UUID marksheetId, UpdateMarksheetRequest request) {
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId.toString());

        Marksheet marksheet = repository.findById(marksheetId).orElseThrow();

        markService.deleteByMarksheet(marksheet);
        markService.saveAll(repository.findById(marksheetId).orElseThrow(), request.getMarkResponseList());

//        for (UpdateMarkRequest markRequest : request.getMarkResponseList()) {
//            markService.UpdateMark(markRequest);
//        }

        marksheet = repository.findById(marksheetId).orElseThrow();

        if (marksheet.getMarksheetSummary() == null) {
            marksheet.setMarksheetSummary(new MarksheetSummary());
        }

        marksheet.setStudentName(request.getStudentName());
        marksheet.setMotherName(request.getMotherName());
        marksheet.setFatherName(request.getFatherName());
        marksheet.setSeatNo(request.getSeatNo());
        marksheet.getMarksheetSummary().setYearOfPassing(request.getYearOfPassing());
        marksheet.setSchoolCentreNo(request.getSchoolCentreNo());
        marksheet.setSchoolIndexNo(request.getSchoolIndexNo());
        marksheet.setGroup(request.getGroup());


        marksheet.getMarksheetSummary().setObtainedGrade(request.getObtainedGrade());
        marksheet.getMarksheetSummary().setTotalObtainedMarks(request.getTotalObtainedMarks());
        marksheet.getMarksheetSummary().setTotalOutOfMarks(request.getTotalOutOfMarks());
        marksheet.getMarksheetSummary().setObtainedPercentage(request.getObtainedPercentage());
        marksheet.getMarksheetSummary().setObtainedPercentile(request.getObtainedPercentile());
        marksheet.getMarksheetSummary().setResultStatus(request.getResultStatus());
        marksheet.setVerifiedByUser(userService.findById(userId).orElseThrow());
        marksheet.setVerificationStatus(VerificationStatus.VERIFIED);

        marksheet.setVerifiedByUser(userService.findById(userId).orElseThrow());

        Board board = boardService.findByShortName(request.getBoard()).orElseThrow();
        marksheet.setBoard(board);
        repository.save(marksheet);

        repository.save(marksheet);

        sseService.sendProjectInfo(projectService.refreshProjectStatistics(projectId.toString()));
        sseService.sendMarksheetInfo(repository.findById(marksheetId).orElseThrow());
    }

    public GetMarksheetStatusResponse getMarksheetStatusInfoById(String projectId, String marksheetId) {
        Marksheet marksheet = repository.findById(UUID.fromString(marksheetId)).orElseThrow();
        if (!marksheet.getProject().getId().equals(UUID.fromString(projectId))) {
            throw new RuntimeException("Marksheet with id " + marksheetId + " " +
                    "dont belong to project id with " + projectId);
        }
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);

        GetMarksheetStatusResponse marksheetResponse = converter.getMarksheetStatusResponse(marksheet);
        return marksheetResponse;
    }

    public GetMarksheetResponse getMarksheetInfoById(String projectId, String marksheetId) {
        Marksheet marksheet = repository.findById(UUID.fromString(marksheetId)).orElseThrow();
        if (!marksheet.getProject().getId().equals(UUID.fromString(projectId))) {
            throw new RuntimeException("Marksheet with id " + marksheetId + " " +
                    "dont belong to project id with " + projectId);
        }
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);

        GetMarksheetResponse marksheetResponse = converter.getMarksheetResponse(marksheet);
        List<GetMarkResponse> markResponseList = markService.getMarkResponseList(marksheet);
        marksheetResponse.setMarkResponseList(markResponseList);

        return marksheetResponse;
    }

    public List<GetMarksheetStatusResponse> getMarksheetResponseList(String projectId) {
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);
        Project project = projectService.findById(projectId).orElseThrow();
        List<Marksheet> marksheetList = repository.findAllByProject(project);

        List<GetMarksheetStatusResponse> marksheetResponseList = new ArrayList<>();
        for (Marksheet marksheet : marksheetList) {
            marksheetResponseList.add(getMarksheetStatusInfoById(projectId, marksheet.getId().toString()));
        }

        return marksheetResponseList;
    }

    void authenticateUserAndProject(String userId, String projectId) {
        UserProject userProject = userProjectService.findById(
                UserProjectId.builder()
                        .userId(UUID.fromString(userId))
                        .projectId(UUID.fromString(projectId))
                        .build()
        ).orElseThrow();
    }

    public List<ProcessMarksheetResponse> processMarksheets(String projectId) {
        Project project = projectService.findById(projectId).orElseThrow();

        List<Marksheet> marksheets = repository.findAllByProject(project);

        List<ProcessMarksheetResponse> responseList = new ArrayList<>();

        for (Marksheet marksheet : marksheets) {
            ProcessMarksheetResponse response = processMarksheet(projectId, marksheet.getId().toString());
            responseList.add(response);
        }

        return responseList;
    }

    public synchronized ProcessMarksheetResponse processMarksheet(String projectId, String marksheetId) {
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);

        Marksheet marksheet = repository.findById(UUID.fromString(marksheetId)).orElseThrow();

        if (!marksheet.getProject().getId().equals(UUID.fromString(projectId))) {
            throw new RuntimeException("Marksheet does not belong to the project");
        }

        if ((marksheet.getProcessingStatus().equals(ProcessingStatus.UNPROCESSED) ||
                marksheet.getProcessingStatus().equals(ProcessingStatus.FAILED)) && !marksheet.getVerificationStatus().equals(VerificationStatus.VERIFIED)) {
            marksheet.setProcessingStatus(ProcessingStatus.QUEUED);
            marksheet.setProcessingStartedAt(LocalDateTime.now());
            marksheet = repository.save(marksheet);
            sseService.sendMarksheetInfo(repository.findById(UUID.fromString(marksheetId)).orElseThrow());
            sseService.sendProjectInfo(projectService.refreshProjectStatistics(projectId));
            Marksheet finalMarksheet = marksheet;
            executorService.submit(() -> processMarksheetInBackground(finalMarksheet));
        }

        return converter.processMarksheetResponse(marksheet);
    }

    private void processMarksheetInBackground(Marksheet marksheet) {
        try {
            marksheet.setProcessingStatus(ProcessingStatus.PROCESSING);
            marksheet = repository.save(marksheet);

            sseService.sendMarksheetInfo(repository.findById(marksheet.getId()).orElseThrow());
            sseService.sendProjectInfo(projectService.refreshProjectStatistics(marksheet.getProject().getId().toString()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonRequest = "{\"file_path\":\"" +
                    marksheet.getUrl().replace("\\", "\\\\") + "\"}";

            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(props.getProcessApiUrl(), entity, Map.class);

            saveStructuredResponse(response, marksheet);
        } catch (Exception e) {
            marksheet = updateFailedMarksheetInfo(marksheet);
            throw e;
        } finally {
            sseService.sendMarksheetInfo(repository.findById(marksheet.getId()).orElseThrow());
            sseService.sendProjectInfo(projectService.refreshProjectStatistics(marksheet.getProject().getId().toString()));
        }
    }

    private void saveStructuredResponse(ResponseEntity<Map> response, Marksheet marksheet) {
        List<Mark> markList = new ArrayList<>();
        switch (response.getBody().get("board").toString()) {
            case "GSEB":
                saveStructuredGsebResponse(response, marksheet, markList);
                updateMarksheetInfoInDbAfterProcessing(markList, marksheet);
                break;
            case "CBSE":
                saveStructuredCbseResponse(response, marksheet, markList);
                updateMarksheetInfoInDbAfterProcessing(markList, marksheet);
                break;
            case "ICSE":
                saveStructuredIcseResponse(response, marksheet, markList);
                updateMarksheetInfoInDbAfterProcessing(markList, marksheet);
                break;
            default:
                marksheet.setProcessingStatus(ProcessingStatus.FAILED);
                marksheet = repository.save(marksheet);
        }

        long seconds = Duration.between(
                marksheet.getProcessingStartedAt(),
                LocalDateTime.now()
        ).toSeconds();

        marksheet.setProcessingDuration(seconds);
        repository.save(marksheet);
    }

    private void saveStructuredCbseResponse(ResponseEntity<Map> response, Marksheet marksheet, List<Mark> markList) {
        CbseResponse cbseResponse = objectMapper.convertValue(
                response.getBody(), CbseResponse.class);

        MarksheetSummary summary = MarksheetSummary.builder()
                .resultStatus(cbseResponse.getResult())
                .yearOfPassing(extractYear(cbseResponse.getDate()))
                .build();

        Board board = boardService.findByShortName(cbseResponse.getBoard()).orElseThrow();

        try {
            marksheet.setCorrected(objectMapper.writeValueAsString(cbseResponse.getCorrected()));
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }

        marksheet.setBoard(board);
        marksheet.setMarksheetSummary(summary);
        marksheet.setSeatNo(cbseResponse.getRollNo().toString());
        marksheet.setStudentName(cbseResponse.getStudentName());
        marksheet.setMotherName(cbseResponse.getMotherName());
        marksheet.setFatherName(cbseResponse.getFatherName());

        double totalObtained = 0;
        double totalMax = 0;


        if (cbseResponse.getMarks() != null) {
            for (CbseMarkResponse markResponse : cbseResponse.getMarks()) {
                Mark mark = Mark.builder()
                        .marksheet(marksheet)
                        .subjectName(markResponse.getSubject())
                        .subjectGrade(markResponse.getPositionalGrade())
                        .subjectCode(markResponse.getSubCode())
                        .obtainedInWords(markResponse.getTotalInWords())
                        .obtained(markResponse.getTotal())
                        .subjectOutOfMarks(100)
                        .build();

                try {
                    mark.setCorrected(objectMapper.writeValueAsString(markResponse.getCorrected()));
                } catch (Exception e) {
                    throw new RuntimeException("Error converting list to JSON", e);
                }

                markList.add(mark);

                double obtained = Optional.ofNullable(mark.getObtained())
                        .orElse((int) 0.0);

                double maxMarks = Optional.ofNullable(mark.getSubjectOutOfMarks())
                        .orElse((int) 0.0);

                totalObtained += obtained;
                totalMax += maxMarks;
            }
        }

        double percentage = totalMax > 0 ?
                Math.round((totalObtained / totalMax) * 10000.0) / 100.0 : 0;
        // Why ? To Keep 2 Decimal Places

        summary.setObtainedPercentage(percentage);
        summary.setTotalObtainedMarks((int) totalObtained);
        summary.setTotalOutOfMarks((int) totalMax);
    }

    private void saveStructuredGsebResponse(ResponseEntity<Map> response, Marksheet marksheet, List<Mark> markList) {
        GsebResponse gsebResponse = objectMapper.convertValue(
                response.getBody(), GsebResponse.class);

        double totalObtained = 0;
        double totalMax = 0;


        MarksheetSummary summary = MarksheetSummary.builder()
                .yearOfPassing(extractYear(gsebResponse.getDate()))
                .obtainedGrade(gsebResponse.getOverAllGrade())
                .obtainedPercentile(gsebResponse.getSciencePercentileRank())
                .totalObtainedMarks(gsebResponse.getObtained())
                .totalOutOfMarks(gsebResponse.getTotal())
                .resultStatus(gsebResponse.getResult())
                .build();

        Board board = boardService.findByShortName(gsebResponse.getBoard()).orElseThrow();

        try {
            marksheet.setCorrected(objectMapper.writeValueAsString(gsebResponse.getCorrected()));
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }

        marksheet.setBoard(board);
        marksheet.setMarksheetSummary(summary);
        marksheet.setSeatNo(gsebResponse.getSeatNo());
        marksheet.setSchoolCentreNo(gsebResponse.getCentreNo());
        marksheet.setSchoolIndexNo(gsebResponse.getSchoolIndexNo());
        marksheet.setGroup(gsebResponse.getGroupName());
        marksheet.setStudentName(gsebResponse.getStudentName());

        if (gsebResponse.getMarks() != null) {
            for (GsebMarkResponse markResponse : gsebResponse.getMarks()) {
                Mark mark = Mark.builder()
                        .marksheet(marksheet)
                        .obtained(markResponse.getObtained())
                        .obtainedInWords(markResponse.getObtainedInWords())
                        .subjectCode(markResponse.getSubCode())
                        .subjectGrade(markResponse.getGrade())
                        .subjectName(markResponse.getSubject())
                        .subjectOutOfMarks(markResponse.getTotal())
                        .build();

                try {
                    mark.setCorrected(objectMapper.writeValueAsString(markResponse.getCorrected()));
                } catch (Exception e) {
                    throw new RuntimeException("Error converting list to JSON", e);
                }

                markList.add(mark);

                double obtained = Optional.ofNullable(mark.getObtained())
                        .orElse((int) 0.0);

                double maxMarks = Optional.ofNullable(mark.getSubjectOutOfMarks())
                        .orElse((int) 0.0);

                totalObtained += obtained;
                totalMax += maxMarks;
            }
        }

        double percentage = totalMax > 0 ?
                Math.round((totalObtained / totalMax) * 10000.0) / 100.0 : 0;
        // Why ? To Keep 2 Decimal Places

        summary.setObtainedPercentage(percentage);
        summary.setTotalObtainedMarks((int) totalObtained);
        summary.setTotalOutOfMarks((int) totalMax);
    }

    private void saveStructuredIcseResponse(ResponseEntity<Map> response,
                                            Marksheet marksheet,
                                            List<Mark> markList) {
        IcseResponse icseResponse = objectMapper.convertValue(
                response.getBody(), IcseResponse.class);

        MarksheetSummary summary = MarksheetSummary.builder()
                .yearOfPassing(extractYear(icseResponse.getDate()))
                .resultStatus(icseResponse.getResult())
                .build();

        Board board = boardService
                .findByShortName(icseResponse.getBoard())
                .orElseThrow();

        try {
            marksheet.setCorrected(objectMapper.writeValueAsString(icseResponse.getCorrected()));
        } catch (Exception e) {
            throw new RuntimeException("Error converting list to JSON", e);
        }

        marksheet.setBoard(board);
        marksheet.setMarksheetSummary(summary);
        marksheet.setSeatNo(String.valueOf(icseResponse.getUniqueId()));
        marksheet.setStudentName(icseResponse.getStudentName());
        marksheet.setMotherName(icseResponse.getMotherName());
        marksheet.setFatherName(icseResponse.getFatherName());

        double totalObtained = 0;
        double totalMax = 0;


        if (icseResponse.getMarks() != null) {
            for (IcseMarkResponse markResponse : icseResponse.getMarks()) {
                Mark mark = Mark.builder()
                        .marksheet(marksheet)
                        .obtained(markResponse.getPercentageMarks())
                        .obtainedInWords(markResponse.getPercentageMarksInWords())
                        .subjectName(markResponse.getSubject())
                        .subjectOutOfMarks(100)
                        .build();

                try {
                    mark.setCorrected(objectMapper.writeValueAsString(markResponse.getCorrected()));
                } catch (Exception e) {
                    throw new RuntimeException("Error converting list to JSON", e);
                }

                markList.add(mark);

                double obtained = Optional.ofNullable(mark.getObtained())
                        .orElse((int) 0.0);

                double maxMarks = mark.getSubjectOutOfMarks();

                totalObtained += obtained;
                totalMax += maxMarks;
            }
        }

        double percentage = totalMax > 0 ?
                Math.round((totalObtained / totalMax) * 10000.0) / 100.0 : 0;
        // Why ? To Keep 2 Decimal Places

        summary.setObtainedPercentage(percentage);
        summary.setTotalObtainedMarks((int) totalObtained);
        summary.setTotalOutOfMarks((int) totalMax);
    }

    private Marksheet updateMarksheetInfoInDbAfterProcessing(List<Mark> markList, Marksheet marksheet) {
        markService.saveAll(markList);
        marksheet.setProcessingStatus(ProcessingStatus.COMPLETED);
        marksheet = repository.save(marksheet);
        return marksheet;
    }

    private Marksheet updateFailedMarksheetInfo(Marksheet marksheet) {
        marksheet.setProcessingStatus(ProcessingStatus.FAILED);

        long seconds = Duration.between(
                marksheet.getProcessingStartedAt(),
                LocalDateTime.now()
        ).toSeconds();

        marksheet.setProcessingDuration(seconds);
        return repository.save(marksheet);
    }

    public List<UploadMarksheetResponse> storeMarksheets(String projectId, List<MultipartFile> files) {
        String userId = authService.getCurrentUserId();
        List<UploadMarksheetResponse> responses = new ArrayList<>();
        files.forEach(file -> {
            UploadMarksheetResponse response = storeMarksheet(userId, projectId, file);
            responses.add(response);
        });
        return responses;
    }

    private UploadMarksheetResponse storeMarksheet(String userId, String projectId, MultipartFile file) {
        authenticateUserAndProject(userId, projectId);

        Project project = projectService.findById(projectId).orElseThrow();

        Marksheet marksheet = Marksheet.builder()
                .project(project)
                .build();

        marksheet = repository.save(marksheet);
        marksheet = saveFile(file, marksheet);
        sseService.sendProjectInfo(projectService.refreshProjectStatistics(projectId));
        return converter.uploadMarksheetResponse(marksheet);
    }

    private Marksheet saveFile(MultipartFile file, Marksheet marksheet) {
        String folderPath = Paths.get(props.getUploadPath()).toString();

        File folder = new File(folderPath);

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                log.info("Folder created at: {}", folderPath);
            } else {
                log.info("Failed to create folder at: {}", folderPath);
            }
        }

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
            marksheet.setUrl(targetFile.getAbsolutePath());
            return repository.save(marksheet);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    private Integer extractYear(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate.getYear();
    }
}
