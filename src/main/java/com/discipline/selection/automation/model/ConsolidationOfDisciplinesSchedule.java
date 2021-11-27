package com.discipline.selection.automation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * Class for writing in .xlxs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"disciplineCipher", "studentName", "group", "schedule"})
public class ConsolidationOfDisciplinesSchedule {

    private String facilityFirstLetter; // should be stored in column by index 0
    private String disciplineCipher;    // should be stored in column by index 1
    private String disciplineName;      // should be stored in column by index 2
    private String studentName;         // should be stored in column by index 3
    private String group;               // should be stored in column by index 4
    private Schedule schedule;          // should be stored in column by index 5
    private boolean isDuplicate;
    private boolean isFacultiesNear;

    public List<String> getValuesForConsolidationOfDisciplineSchedule() {
        List<String> valuesForConsolidationOfDisciplineSchedule = new ArrayList<>();
        valuesForConsolidationOfDisciplineSchedule.add(facilityFirstLetter);
        valuesForConsolidationOfDisciplineSchedule.add(disciplineCipher);
        valuesForConsolidationOfDisciplineSchedule.add(disciplineName);
        valuesForConsolidationOfDisciplineSchedule.add(studentName);
        valuesForConsolidationOfDisciplineSchedule.add(group);
        valuesForConsolidationOfDisciplineSchedule.add(schedule.scheduleForConsolidationOfDisciplines());
        valuesForConsolidationOfDisciplineSchedule.add(nonNull(schedule.getFacultyType()) ?
                schedule.getFacultyType().toString() : null);
        valuesForConsolidationOfDisciplineSchedule.add(schedule.getFacultyAddress());
        return valuesForConsolidationOfDisciplineSchedule;
    }

}
