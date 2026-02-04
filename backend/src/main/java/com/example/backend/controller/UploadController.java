package com.example.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {

    // body: form-data, key: "files"
    @PostMapping
    public String uploadFiles(List<MultipartFile> files) {
        List<String> results = new ArrayList<>();

        for (MultipartFile file : files) {

        }

        return "Processed all files!";
    }
}

