package com.discipline.selection.automation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"disciplineCipher", "studentName", "group", "schedule"})
public class ConsolidationOfDisciplinesSchedule {

    private String facilityFirstLetter;
    private String disciplineCipher;
    private String disciplineName;
    private String studentName;
    private String group;
    private Schedule schedule;
    private boolean isDuplicate;

    public List<String> getValuesForConsolidationOfDisciplineSchedule() {
        List<String> valuesForConsolidationOfDisciplineSchedule = new ArrayList<>();
        valuesForConsolidationOfDisciplineSchedule.add(facilityFirstLetter);
        valuesForConsolidationOfDisciplineSchedule.add(disciplineCipher);
        valuesForConsolidationOfDisciplineSchedule.add(disciplineName);
        valuesForConsolidationOfDisciplineSchedule.add(studentName);
        valuesForConsolidationOfDisciplineSchedule.add(group);
        valuesForConsolidationOfDisciplineSchedule.add(schedule.scheduleForConsolidationOfDisciplines());
        return valuesForConsolidationOfDisciplineSchedule;
    }

}
