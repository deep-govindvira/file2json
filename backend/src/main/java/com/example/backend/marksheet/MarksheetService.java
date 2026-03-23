package com.example.backend.marksheet;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MarksheetService {

    void assignMarksheet(String projectId, String marksheetId, String userId);

    void assignMarksheets(String projectId, List<String> marksheetIds, String userId);

    void updateMarksheet(UUID projectId, UUID marksheetId, UpdateMarksheetRequest request);

    GetMarksheetStatusResponse getMarksheetStatusInfoById(String projectId, String marksheetId);

    GetMarksheetResponse getMarksheetInfoById(String projectId, String marksheetId);

    List<GetMarksheetStatusResponse> getMarksheetResponseList(String projectId);

    GetMarksheetResponse getMarksheetInfoByIdForVerifier(String projectId, String marksheetId);

    List<GetMarksheetStatusResponse> getMarksheetResponseListForVerifier(String projectId);

    void authenticateUserAndProject(String userId, String projectId);

    List<ProcessMarksheetResponse> processMarksheets(String projectId);

    ProcessMarksheetResponse processMarksheet(String projectId, String marksheetId);

    List<UploadMarksheetResponse> storeMarksheets(String projectId, List<MultipartFile> files);

    void stopAllQueuedProcessing(String projectId);

    byte[] exportToExcel(String projectId, ExportRequest request) throws Exception;
}
