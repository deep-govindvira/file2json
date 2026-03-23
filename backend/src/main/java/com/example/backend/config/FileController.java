package com.example.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;

@RestController
@RequestMapping("/files/{key}")
@RequiredArgsConstructor
public class FileController {

    private final S3Client s3Client;
    private final AppProps appProps;

    private static String getString(String key) {
        String contentType = "application/octet-stream";

        if (key.toLowerCase().endsWith(".png")) {
            contentType = "image/png";
        } else if (key.toLowerCase().endsWith(".jpg") || key.toLowerCase().endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (key.toLowerCase().endsWith(".tiff")) {
            contentType = "image/tiff";
        } else if (key.toLowerCase().endsWith(".bmp")) {
            contentType = "image/bmp";
        } else if (key.toLowerCase().endsWith(".pdf")) {
            contentType = "application/pdf";
        }
        return contentType;
    }

    @GetMapping
    public ResponseEntity<InputStreamResource> getFile(@PathVariable String key) throws IOException {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(appProps.getS3().getBucket())
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(request);

        InputStreamResource resource = new InputStreamResource(s3Object);

        String contentType = getString(key);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)) // image/png, application/pdf, etc.
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                .body(resource);
    }
}