package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final MarkRepository repository;
    private final MarkConverter converter;

    public List<Mark> saveAll(List<Mark> markList) {
        return repository.saveAll(markList);
    }

    public List<GetMarkResponse> getMarkResponseList(Marksheet marksheet) {
        List<GetMarkResponse> markResponseList = new ArrayList<>();
        List<Mark> markList = repository.findByMarksheet(marksheet);
        for (Mark mark : markList) {
            markResponseList.add(converter.getMarksheetResponse(mark));
        }
        return markResponseList;
    }
}
