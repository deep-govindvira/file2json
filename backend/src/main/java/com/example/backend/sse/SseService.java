package com.example.backend.sse;

import com.example.backend.auth.service.AuthService;
import com.example.backend.marksheet.Marksheet;
import com.example.backend.project.Project;
import com.example.backend.sse.dtos.MarksheetStatusUpdateEvent;
import com.example.backend.sse.dtos.ProjectStatusUpdateEvent;
import com.example.backend.user.User;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final AuthService authService;
    private final UserProjectService userProjectService;
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();


    public SseEmitter createEmitter(String userId) {
        SseEmitter emitter = new SseEmitter(0L);


        emitters
                .computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        return emitter;
    }

    private void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
            if (userEmitters.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    public void sendEvent(String userId, String eventName, Object data) {

        List<SseEmitter> userEmitters = emitters.get(userId);

        if (userEmitters == null) return;

        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : userEmitters) {
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(eventName)
                                .data(data)
                );
            } catch (Exception ex) {
                deadEmitters.add(emitter);
                emitter.complete();
            }
        }

        userEmitters.removeAll(deadEmitters);
    }

    @Scheduled(fixedRate = 20000)
    public void heartbeat() {
        for (List<SseEmitter> userEmitters : emitters.values()) {
            for (SseEmitter emitter : userEmitters) {
                try {
                    emitter.send(SseEmitter.event().comment("keep-alive"));
                } catch (Exception e) {
                    emitter.complete();
                }
            }
        }
    }

    public void sendProjectInfo(Project project) {
        List<User> userList = userProjectService.getUsersByProjectId(project.getId().toString());

        ProjectStatusUpdateEvent event =
                ProjectStatusUpdateEvent.builder()
                        .projectId(project.getId().toString())
                        .totalMarksheets(project.getTotalMarksheets())
                        .projectStatus(project.getStatus())
                        .projectProcessingDuration(project.getProcessingDuration())
                        .processingFailedMarksheets(project.getProcessingFailedMarksheets())
                        .processedMarksheets(project.getProcessedMarksheets())
                        .build();

        for (User user : userList) {
            sendEvent(user.getId().toString(), SseEvents.PROJECT_STATUS_UPDATE, event);
        }
    }

    public void sendMarksheetInfo(Marksheet marksheet) {

        List<User> userList = userProjectService.getUsersByProjectId(marksheet.getProject().getId().toString());


        MarksheetStatusUpdateEvent event =
                MarksheetStatusUpdateEvent.builder()
                        .marksheetId(marksheet.getId().toString())
                        .processingStatus(marksheet.getProcessingStatus().name())
                        .projectId(marksheet.getProject().getId().toString())
                        .verificationStatus(marksheet.getVerificationStatus().name())
                        .build();

        if (marksheet.getVerifiedByUser() != null) {
            event.setVerifiedByUser(marksheet.getVerifiedByUser().getEmail());
        }

        for (User user : userList) {
            sendEvent(user.getId().toString(), SseEvents.MARKSHEET_STATUS_UPDATE, event);
        }
    }
}
