package com.example.backend.mark;

import com.example.backend.marksheet.Marksheet;
import com.example.backend.project.Project;

import java.util.List;

public interface MarkService {

    List<Mark> findAllByProject(Project project);

    List<Mark> saveAll(List<Mark> markList);

    void saveAll(Marksheet marksheet, List<UpdateMarkRequest> markList);

    void deleteByMarksheet(Marksheet marksheet);

    void UpdateMark(UpdateMarkRequest request);

    List<GetMarkResponse> getMarkResponseList(Marksheet marksheet);
}
