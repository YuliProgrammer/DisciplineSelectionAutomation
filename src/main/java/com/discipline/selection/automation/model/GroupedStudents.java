package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupedStudents {
    private Map<String, List<Student>> studentsGroupedByDisciplines = new LinkedHashMap<>();
    private Map<String, List<Student>> studentsGroupedByGroup = new LinkedHashMap<>();
}
