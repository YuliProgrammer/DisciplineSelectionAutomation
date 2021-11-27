package com.discipline.selection.automation.model;

import static com.discipline.selection.automation.util.Constants.ONE;
import static com.discipline.selection.automation.util.Constants.SEMICOLON;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discipline {

    private String disciplineCipher;
    private String disciplineName;
    private String facilityCipher;
    private String cathedraCipher;
    private String lecturesHoursPerWeek;
    private String practicalHoursPerWeek;
    private String laboratoryHoursPerWeek;
    private Integer maxStudentsCount;
    private Integer currentStudentsCount;

    private Integer numberOfStudentsInFlow;
    private Integer numberOfStudentsInGroup;
    private Integer numberOfStudentsInSubGroup;

    private Integer studentsCount;
    private List<Long> studentsCountByGroups;
    private Map<String, List<Schedule>> schedule;

    /**
     * @return list of discipline values that related to the chosen discipline list
     */
    public List<String> getValuesForChosenDiscipline() {
        List<String> values = new ArrayList<>();
        values.add(disciplineCipher);
        values.add(disciplineName);
        values.add(facilityCipher);
        values.add(cathedraCipher);
        values.add(lecturesHoursPerWeek);
        values.add(practicalHoursPerWeek);
        values.add(laboratoryHoursPerWeek);
        values.add(getString(numberOfStudentsInFlow));
        values.add(getString(numberOfStudentsInGroup));
        values.add(getString(numberOfStudentsInSubGroup));
        values.add(getString(maxStudentsCount));
        return values;
    }

    /**
     * @return list of discipline values that related to the header CONSOLIDATION_OF_DISCIPLINES_HEADER
     */
    public List<String> getValuesForConsolidationOfDiscipline() {
        List<String> values = new ArrayList<>();
        values.add(disciplineName + "(" + facilityCipher + ")");
        values.add(disciplineCipher);
        values.add(lecturesHoursPerWeek);
        values.add(practicalHoursPerWeek);
        values.add(laboratoryHoursPerWeek);

        int flowCount = studentsCount <= numberOfStudentsInFlow ? ONE : studentsCount / numberOfStudentsInFlow;
        values.add(String.valueOf(flowCount));

        int groupCount = studentsCount <= numberOfStudentsInGroup ? ONE : studentsCount / numberOfStudentsInGroup;
        values.add(String.valueOf(groupCount));

        if (laboratoryHoursPerWeek.isEmpty()) {
            values.add("");
        } else {
            int subgroupCount = (studentsCount <= numberOfStudentsInSubGroup || numberOfStudentsInSubGroup <= 0) ? ONE :
                    studentsCount / numberOfStudentsInSubGroup;
            values.add(String.valueOf(subgroupCount));
        }

        values.add(schedule.get(disciplineCipher) == null ? "" : new LinkedHashSet<>(schedule.get(disciplineCipher))
                .stream()
                .map(Schedule::scheduleForConsolidationOfDisciplines)
                .collect(Collectors.joining(SEMICOLON)));

        values.add(String.valueOf(studentsCount));
        studentsCountByGroups
                .forEach(studentsCount -> values.add(studentsCount == 0 ? "" : String.valueOf(studentsCount)));

        return values;
    }

    private String getString(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }

}

