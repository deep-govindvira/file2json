package com.example.backend.config;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final AppProps props; // inject your config

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {

        String maxFileSize = props.getSpring().getServlet().getMultipart().getMaxFileSize();
        String maxRequestSize = props.getSpring().getServlet().getMultipart().getMaxRequestSize();

        String message = "Upload failed! File size must not exceed " + maxFileSize +
                " per file and total upload must not exceed " + maxRequestSize + ".";

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JDBCConnectionException.class)
    public ResponseEntity<Object> handleJdbcConnectionException(JDBCConnectionException ex) {

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        response.put("error", "Database Connection Error");

        if (ex.getCause() != null) {
            response.put("message", ex.getCause().getMessage());
        } else {
            response.put("message", ex.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AuthorizationDeniedException ex) {
        return new ResponseEntity<>("Access Denied: " + ex.getMessage(),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<Map<String, Object>> handleMessagingException(MessagingException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Email formatting/creation error");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Map<String, Object>> handleMailException(MailException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Failed to send email");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong while processing the request.");
    }
}