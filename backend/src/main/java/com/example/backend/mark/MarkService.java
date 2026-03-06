package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MarkService {

    private final MarkRepository repository;
    private final MarkConverter converter;

    public List<Mark> saveAll(List<Mark> markList) {
        return repository.saveAll(markList);
    }

    public void saveAll(Marksheet marksheet, List<UpdateMarkRequest> markList) {
        for (UpdateMarkRequest request : markList) {
            Mark mark = Mark.builder().build();
            mark.setMarksheet(marksheet);
            mark.setObtained(request.getObtained());
            mark.setSubjectOutOfMarks(request.getSubjectOutOfMarks());
            mark.setObtainedInWords(request.getObtainedInWords());
            mark.setSubjectCode(request.getSubjectCode());
            mark.setSubjectGrade(request.getSubjectGrade());
            mark.setSubjectName(request.getSubjectName());
            mark.setCorrected(request.getCorrected());
            repository.save(mark);
        }
    }

    @Transactional
    public void deleteByMarksheet(Marksheet marksheet) {
        repository.deleteByMarksheet(marksheet);
        repository.flush();
    }

    public void UpdateMark(UpdateMarkRequest request) {
        Mark mark = repository.findById(UUID.fromString(request.getId())).orElseThrow();
        mark.setObtained(request.getObtained());
        mark.setSubjectOutOfMarks(request.getSubjectOutOfMarks());
        mark.setObtainedInWords(request.getObtainedInWords());
        mark.setSubjectCode(request.getSubjectCode());
        mark.setSubjectGrade(request.getSubjectGrade());
        mark.setSubjectName(request.getSubjectName());
        repository.save(mark);
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
