package com.example.backend.marksheet;

import com.example.backend.auth.entity.Role;
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
import com.example.backend.notification.NotificationPort;
import com.example.backend.project.Project;
import com.example.backend.project.ProjectService;
import com.example.backend.sse.SseService;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProject;
import com.example.backend.user_project.UserProjectId;
import com.example.backend.user_project.UserProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
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
public class MarksheetServiceImpl implements MarksheetService {

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
    private final NotificationPort notificationPort;
    private final S3Client s3Client;


    public void assignMarksheet(String projectId, String marksheetId, String userId) {
        authenticateUserAndProject(authService.getCurrentUserId(), projectId);

        Marksheet marksheet = repository.findById(UUID.fromString(marksheetId))
                .orElseThrow();

        if (!(marksheet.getProcessingStatus().equals(ProcessingStatus.COMPLETED) ||
                marksheet.getProcessingStatus().equals(ProcessingStatus.FAILED))
        ) {
            throw new RuntimeException("Cant assign unprocessed marksheet");
        }

        User user = userService.findById(userId)
                .orElseThrow();

        marksheet.setAssignedToUser(user);

        repository.save(marksheet);
    }

    public void assignMarksheets(String projectId, List<String> marksheetIds, String userId) {

        authenticateUserAndProject(authService.getCurrentUserId(), projectId);

        User user = userService.findById(userId)
                .orElseThrow();

        List<UUID> ids = marksheetIds.stream()
                .map(UUID::fromString)
                .toList();

        List<Marksheet> marksheets = repository.findAllById(ids);

        for (Marksheet marksheet : marksheets) {

            if (!(marksheet.getProcessingStatus().equals(ProcessingStatus.COMPLETED) ||
                    marksheet.getProcessingStatus().equals(ProcessingStatus.FAILED))) {
                continue;
            }

            marksheet.setAssignedToUser(user);
        }

        repository.saveAll(marksheets);

        executorService.submit(() -> {
            try {

                String projectLink = props.getAllowedOrigin() + "/project/" + projectId + "/view";

                String htmlMessage = """
                        <html>
                        <body style="font-family: Arial, sans-serif; background-color: #f5f6fa; padding: 20px;">
                        
                            <div style="max-width: 600px; margin: auto; background: #ffffff; padding: 24px; border-radius: 10px;">
                        
                                <h2 style="color: #2f3640; margin-bottom: 10px;">Marksheets Assigned</h2>
                        
                                <p>Hello <b>%s</b>,</p>
                        
                                <p>
                                    You have been assigned <b>%d marksheets</b> for the project 
                                    <b>%s</b>.
                                </p>
                        
                                <p style="margin-top: 20px;">
                                    Please review and process them at your earliest convenience.
                                </p>
                        
                                <div style="text-align:center; margin: 30px 0;">
                                    <a href="%s"
                                       style="background-color:#0984e3;color:#fff;padding:14px 24px;
                                              text-decoration:none;border-radius:6px;font-weight:bold;
                                              display:inline-block;">
                                        View Marksheets
                                    </a>
                                </div>
                        
                                <p style="font-size: 13px; color: #555;">
                                    If you were not expecting this assignment, please contact your administrator.
                                </p>
                        
                                <hr style="margin: 20px 0;"/>
                        
                                <p style="font-size:12px;color:gray;">
                                    This is an automated email. Please do not reply.
                                </p>
                        
                            </div>
                        
                        </body>
                        </html>
                        """.formatted(
                        user.getName(),
                        marksheetIds.size(),
                        marksheets.getFirst().getProject().getName(),
                        projectLink
                );

                notificationPort.notify(
                        "Marksheets Assigned - " + marksheets.getFirst().getProject().getName(),
                        user.getEmail(),
                        htmlMessage
                );

            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        });
    }

    public void updateMarksheet(UUID projectId, UUID marksheetId, UpdateMarksheetRequest request) {
        String userId = authService.getCurrentUserId();

        authenticateUserAndProject(userId, projectId.toString());

        Marksheet marksheet = repository.findById(marksheetId).orElseThrow();

        User user = userService.findById(userId).orElseThrow();

        if (user.getRole().equals(Role.VERIFIER) &&
                !marksheet.getAssignedToUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Invalid verifier for markhseet");
        }

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

        User user = userService.findById(userId).orElseThrow();

        if (user.getRole().equals(Role.VERIFIER) &&
                !marksheet.getAssignedToUser().getId().toString().equals(userId)) {
            throw new RuntimeException("Invalid verifier for markhseet");
        }

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

    public GetMarksheetResponse getMarksheetInfoByIdForVerifier(String projectId, String marksheetId) {
        Marksheet marksheet = repository.findById(UUID.fromString(marksheetId)).orElseThrow();
        if (!marksheet.getProject().getId().equals(UUID.fromString(projectId))) {
            throw new RuntimeException("Marksheet with id " + marksheetId + " " +
                    "dont belong to project id with " + projectId);
        }

        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);

        GetMarksheetResponse marksheetResponse = GetMarksheetResponse.builder().build();

        if (marksheet.getAssignedToUser() != null &&
                marksheet.getAssignedToUser().getId().toString().equals(userId)) {
            marksheetResponse = converter.getMarksheetResponse(marksheet);
            List<GetMarkResponse> markResponseList = markService.getMarkResponseList(marksheet);

            marksheetResponse.setMarkResponseList(markResponseList);
        }

        return marksheetResponse;
    }

    public List<GetMarksheetStatusResponse> getMarksheetResponseListForVerifier(String projectId) {
        String userId = authService.getCurrentUserId();
        authenticateUserAndProject(userId, projectId);
        Project project = projectService.findById(projectId).orElseThrow();
        List<Marksheet> marksheetList = repository.findAllByProject(project);

        List<GetMarksheetStatusResponse> marksheetResponseList = new ArrayList<>();
        for (Marksheet marksheet : marksheetList) {
            if (marksheet.getAssignedToUser() != null &&
                    marksheet.getAssignedToUser().getId().toString().equals(userId)) {
                marksheetResponseList.add(getMarksheetStatusInfoById(projectId, marksheet.getId().toString()));
            }
        }

        return marksheetResponseList;
    }

    public void authenticateUserAndProject(String userId, String projectId) {
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
                marksheet.getProcessingStatus().equals(ProcessingStatus.FAILED)) &&
                !marksheet.getVerificationStatus().equals(VerificationStatus.VERIFIED)) {
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
        Marksheet latest = repository.findById(marksheet.getId()).orElseThrow();

        if (latest.getProcessingStatus() != ProcessingStatus.QUEUED) {
            return;
        }

        try {
            latest.setProcessingStatus(ProcessingStatus.PROCESSING);
            latest = repository.save(latest);

            sseService.sendMarksheetInfo(latest);
            sseService.sendProjectInfo(
                    projectService.refreshProjectStatistics(
                            Objects.requireNonNull(latest.getProject()).getId().toString()
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String jsonRequest = """
                    {
                      "bucket": "%s",
                      "key": "%s"
                    }
                    """.formatted(props.getS3().getBucket(), latest.getUrl());

            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(props.getProcessApiUrl(), entity, Map.class);

            saveStructuredResponse(response, latest);

        } catch (Exception e) {
            updateFailedMarksheetInfo(latest);
            throw e;
        } finally {
            Marksheet updated = repository.findById(marksheet.getId()).orElseThrow();

            sseService.sendMarksheetInfo(updated);
            sseService.sendProjectInfo(
                    projectService.refreshProjectStatistics(
                            updated.getProject().getId().toString()
                    )
            );
        }
    }

//    private void processMarksheetInBackground(Marksheet marksheet) {
//        Marksheet latest = repository.findById(marksheet.getId()).orElseThrow();
//
//        if (latest.getProcessingStatus() != ProcessingStatus.QUEUED) {
//            return;
//        }
//
//        try {
//            marksheet.setProcessingStatus(ProcessingStatus.PROCESSING);
//            marksheet = repository.save(marksheet);
//
//            sseService.sendMarksheetInfo(repository.findById(marksheet.getId()).orElseThrow());
//            sseService.sendProjectInfo(projectService.refreshProjectStatistics(marksheet.getProject().getId().toString()));
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            String jsonRequest = "{\"file_path\":\"" +
//                    marksheet.getUrl().replace("\\", "\\\\") + "\"}";
//
//            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);
//
//            ResponseEntity<Map> response = restTemplate.postForEntity(props.getProcessApiUrl(), entity, Map.class);
//
//            saveStructuredResponse(response, marksheet);
//        } catch (Exception e) {
//            marksheet = updateFailedMarksheetInfo(marksheet);
//            throw e;
//        } finally {
//            sseService.sendMarksheetInfo(repository.findById(marksheet.getId()).orElseThrow());
//            sseService.sendProjectInfo(projectService.refreshProjectStatistics(marksheet.getProject().getId().toString()));
//        }
//    }

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
//        marksheet = saveFile(file, marksheet);
        marksheet = saveFileInS3(file, marksheet);
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

    private Marksheet saveFileInS3(MultipartFile file, Marksheet marksheet) {

        final String bucketName = props.getS3().getBucket();

        String originalFileName = file.getOriginalFilename();
        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }


        //        String key = "marksheets/" + marksheet.getId() + extension;
        String key = marksheet.getId() + extension;

        try {

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            marksheet.setUrl(key);

            return repository.save(marksheet);

        } catch (Exception e) {
            e.printStackTrace(); // ⭐ important for debugging
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private Integer extractYear(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        return localDate.getYear();
    }

    public void stopAllQueuedProcessing(String projectId) {

        UUID pid = UUID.fromString(projectId);

        List<Marksheet> queuedMarksheets =
                repository.findByProjectIdAndProcessingStatus(
                        pid, ProcessingStatus.QUEUED);

        for (Marksheet m : queuedMarksheets) {
            m.setProcessingStatus(ProcessingStatus.UNPROCESSED);
        }

        repository.saveAll(queuedMarksheets);

        for (Marksheet m : queuedMarksheets) {
            sseService.sendMarksheetInfo(m);
        }

        sseService.sendProjectInfo(projectService.refreshProjectStatistics(projectId));
    }

    private String mapSubjectField(String col) {
        switch (col) {
            case "Subject Name":
                return "name";
            case "Subject Code":
                return "code";
            case "Subject Obtained In Words":
                return "obtained_in_words";
            case "Subject Obtained Marks":
                return "obtained_marks";
            case "Subject Out Of Marks":
                return "out_of_marks";
            case "Subject Grade":
                return "grade";
            default:
                return "";
        }
    }

    public byte[] exportToExcel(String projectId, ExportRequest request) throws Exception {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Marksheets");

        Project project = projectService.findById(projectId).orElseThrow();
        List<Marksheet> marksheets = repository.findAllByProject(project);

        List<String> columns = request.getColumns();

        // ✅ Split columns
        List<String> baseColumns = columns.stream()
                .filter(c -> !c.startsWith("Subject"))
                .toList();

        List<String> subjectColumns = columns.stream()
                .filter(c -> c.startsWith("Subject"))
                .toList();

        Row header = sheet.createRow(0);
        int colIndex = 0;

        // =========================
        // ✅ BASE HEADER
        // =========================
        for (String column : baseColumns) {
            header.createCell(colIndex++).setCellValue(column);
        }

        // =========================
        // ✅ SUBJECT PREPARATION
        // =========================
        Map<UUID, List<Mark>> marksByMarksheet = new HashMap<>();
        int maxSubjects = 0;

        if (!subjectColumns.isEmpty()) {

            List<Mark> allMarks = markService.findAllByProject(project);

            for (Mark mark : allMarks) {
                UUID msId = mark.getMarksheet().getId();
                marksByMarksheet
                        .computeIfAbsent(msId, k -> new ArrayList<>())
                        .add(mark);
            }

            // find max subjects
            for (List<Mark> marks : marksByMarksheet.values()) {
                maxSubjects = Math.max(maxSubjects, marks.size());
            }

            // =========================
            // ✅ SUBJECT HEADER
            // =========================
            for (int i = 1; i <= maxSubjects; i++) {
                for (String col : subjectColumns) {
                    header.createCell(colIndex++)
                            .setCellValue("subject_" + i + "_" + mapSubjectField(col));
                }
            }
        }

        // =========================
        // ✅ DATA ROWS
        // =========================
        int rowNum = 1;

        for (Marksheet m : marksheets) {

            Row row = sheet.createRow(rowNum++);
            int cellIndex = 0;

            // 🔹 BASE DATA
            for (String col : baseColumns) {

                switch (col) {
                    case "Student Name":
                        setCellValueOrBlank(row, cellIndex++, m.getStudentName());
                        break;
                    case "Seat No":
                        setCellValueOrBlank(row, cellIndex++, m.getSeatNo());
                        break;
                    case "School Index No":
                        setCellValueOrBlank(row, cellIndex++, m.getSchoolIndexNo());
                        break;
                    case "School Centre No":
                        setCellValueOrBlank(row, cellIndex++, m.getSchoolCentreNo());
                        break;
                    case "Mother Name":
                        setCellValueOrBlank(row, cellIndex++, m.getMotherName());
                        break;
                    case "Father Name":
                        setCellValueOrBlank(row, cellIndex++, m.getFatherName());
                        break;
                    case "Group":
                        setCellValueOrBlank(row, cellIndex++, m.getGroup());
                        break;
                    case "Board":
                        setCellValueOrBlank(row, cellIndex++, m.getBoard() != null ? m.getBoard().getShortName() : null);
                        break;
                    case "Verifier Name":
                        setCellValueOrBlank(row, cellIndex++, m.getVerifiedByUser() != null ? m.getVerifiedByUser().getName() : null);
                        break;
                    case "Verifier Email":
                        setCellValueOrBlank(row, cellIndex++, m.getVerifiedByUser() != null ? m.getVerifiedByUser().getEmail() : null);
                        break;
                    case "Year of Passing":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? String.valueOf(m.getMarksheetSummary().getYearOfPassing()) : null);
                        break;
                    case "Total Obtained Marks":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? String.valueOf(m.getMarksheetSummary().getTotalObtainedMarks()) : null);
                        break;
                    case "Total Out Of Marks":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? String.valueOf(m.getMarksheetSummary().getTotalOutOfMarks()) : null);
                        break;
                    case "Total Obtained Percentage":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? String.valueOf(m.getMarksheetSummary().getObtainedPercentage()) : null);
                        break;
                    case "Total Obtained Percentile":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? String.valueOf(m.getMarksheetSummary().getObtainedPercentile()) : null);
                        break;
                    case "Overall Obtained Grade":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? m.getMarksheetSummary().getObtainedGrade() : null);
                        break;
                    case "Result Status":
                        setCellValueOrBlank(row, cellIndex++, m.getMarksheetSummary() != null
                                ? m.getMarksheetSummary().getResultStatus() : null);
                        break;
                }
            }

            // 🔹 SUBJECT DATA
            if (!subjectColumns.isEmpty()) {

                List<Mark> marks = marksByMarksheet
                        .getOrDefault(m.getId(), new ArrayList<>());

                for (int s = 0; s < maxSubjects; s++) {

                    Mark mark = s < marks.size() ? marks.get(s) : null;

                    for (String col : subjectColumns) {

                        switch (col) {
                            case "Subject Name":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? mark.getSubjectName() : null);
                                break;
                            case "Subject Code":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? mark.getSubjectCode() : null);
                                break;
                            case "Subject Obtained In Words":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? mark.getObtainedInWords() : null);
                                break;
                            case "Subject Obtained Marks":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? String.valueOf(mark.getObtained()) : null);
                                break;
                            case "Subject Out Of Marks":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? String.valueOf(mark.getSubjectOutOfMarks()) : null);
                                break;
                            case "Subject Grade":
                                setCellValueOrBlank(row, cellIndex++, mark != null ? mark.getSubjectGrade() : null);
                                break;
                        }
                    }
                }
            }
        }

        // =========================
        // ✅ AUTO SIZE
        // =========================
        for (int i = 0; i < colIndex; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    private void setCellValueOrBlank(Row row, int cellIndex, String value) {
        if (value != null) {
            row.createCell(cellIndex).setCellValue(value);
        } else {
            row.createCell(cellIndex).setBlank();
        }
    }
}
