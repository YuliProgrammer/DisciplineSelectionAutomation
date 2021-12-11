package com.discipline.selection.automation.model;

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
public class GroupedSchedule {
    private Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher = new LinkedHashMap<>();
    private Map<String, List<Schedule>> schedulesGroupedByTeacher = new LinkedHashMap<>();
}
