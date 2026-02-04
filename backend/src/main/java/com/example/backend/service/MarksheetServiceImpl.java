package com.example.backend.service;

import com.example.backend.converter.GsebMarksheetConverter;
import com.example.backend.converter.GsebSubjectConverter;
import com.example.backend.dtos.GsebMarksheetResponse;
import com.example.backend.entity.GsebMarksheet;
import com.example.backend.entity.GsebSubject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MarksheetServiceImpl implements MarksheetService {

    private final GsebMarksheetService gsebMarksheetService;

    public MarksheetServiceImpl(GsebMarksheetService gsebMarksheetService) {
        this.gsebMarksheetService = gsebMarksheetService;
    }

    @Override
    public void save(ResponseEntity<Map> response) {
        switch (response.getBody().get("board").toString()) {
            case "GSEB":
                ObjectMapper objectMapper = new ObjectMapper();
                GsebMarksheetResponse gsebMarksheetResponse = objectMapper.convertValue(response.getBody(), GsebMarksheetResponse.class);
                GsebMarksheet gsebMarksheet = GsebMarksheetConverter.toGsebMarksheet(gsebMarksheetResponse);

                gsebMarksheetResponse.getSubjects().forEach(s -> {
                    GsebSubject sm = GsebSubjectConverter.toGsebSubject(s);
                    sm.setMarksheet(gsebMarksheet);
                    gsebMarksheet.getSubjects().add(sm);
                });

                gsebMarksheetService.save(gsebMarksheet);

                log.info("GsebMarksheetResponse: {}", gsebMarksheetResponse);
                break;

            case "CBSE":
                break;
        }
    }
}
