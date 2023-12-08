package com.discipline.selection.automation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.ONE;
import static com.discipline.selection.automation.util.Constants.SEMICOLON;

/**
 * Class for reading data about discipline from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "disciplines")
@ToString(exclude = "schedule")
@EqualsAndHashCode(exclude = "schedule")
public class Discipline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cipher", unique = true, nullable = false)
    private String disciplineCipher;

    /**
     * The discipline name doesn't unique,
     * cause on different facilities can be disciplines with the same names but different ciphers
     */
    @Column(name = "name", nullable = false)
    private String disciplineName;

    @Column(name = "facility_cipher", nullable = false, length = 12)
    private String facilityCipher;

    @Column(name = "cathedra_cipher", nullable = false, length = 10)
    private String cathedraCipher;

    @Column(name = "lectures_hours", nullable = false)
    private Integer lecturesHoursPerWeek;

    @Column(name = "practice_hours", nullable = false)
    private Integer practicalHoursPerWeek;

    @Column(name = "laboratory_hours", nullable = false)
    private Integer laboratoryHoursPerWeek;

    @Transient
    private Integer currentStudentsCount;

    @Column(name = "max_flow_students", nullable = false)
    private Integer numberOfStudentsInFlow;

    @Column(name = "max_group_students", nullable = false)
    private Integer numberOfStudentsInGroup;

    @Column(name = "max_subgroup_students", nullable = false)
    private Integer numberOfStudentsInSubGroup;

    @Column(name = "max_students_count", nullable = false)
    private Integer maxStudentsCount;

    @Transient
    private Integer studentsCount;

    @Transient
    private List<Long> studentsCountByGroups;

    @Transient
    private Map<String, List<Schedule>> schedule;

    public Discipline(Discipline discipline) {
        this.id = discipline.id;
        this.disciplineCipher = discipline.disciplineCipher;
        this.disciplineName = discipline.disciplineName;
        this.facilityCipher = discipline.facilityCipher;
        this.cathedraCipher = discipline.cathedraCipher;
        this.lecturesHoursPerWeek = discipline.lecturesHoursPerWeek;
        this.practicalHoursPerWeek = discipline.practicalHoursPerWeek;
        this.laboratoryHoursPerWeek = discipline.laboratoryHoursPerWeek;
        this.currentStudentsCount = discipline.currentStudentsCount;
        this.numberOfStudentsInFlow = discipline.numberOfStudentsInFlow;
        this.numberOfStudentsInGroup = discipline.numberOfStudentsInGroup;
        this.numberOfStudentsInSubGroup = discipline.numberOfStudentsInSubGroup;
        this.maxStudentsCount = discipline.maxStudentsCount;
        this.studentsCount = discipline.studentsCount;
        this.studentsCountByGroups = discipline.studentsCountByGroups;
    }

    /**
     * @return list of discipline values that related to the chosen discipline list
     */
    public List<String> getValuesForChosenDiscipline() {
        List<String> values = new ArrayList<>();
        values.add(disciplineCipher);
        values.add(disciplineName);
        values.add(facilityCipher);
        values.add(cathedraCipher);
        values.add(String.valueOf(lecturesHoursPerWeek));
        values.add(String.valueOf(practicalHoursPerWeek));
        values.add(String.valueOf(laboratoryHoursPerWeek));
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
        values.add(disciplineName + " (" + facilityCipher + ")");
        values.add(disciplineCipher);
        values.add(String.valueOf(lecturesHoursPerWeek));
        values.add(String.valueOf(practicalHoursPerWeek));
        values.add(String.valueOf(laboratoryHoursPerWeek));

        int flowCount = studentsCount <= numberOfStudentsInFlow ? ONE : studentsCount / numberOfStudentsInFlow;
        values.add(String.valueOf(flowCount));

        int groupCount = studentsCount <= numberOfStudentsInGroup ? ONE : studentsCount / numberOfStudentsInGroup;
        values.add(String.valueOf(groupCount));

        if (laboratoryHoursPerWeek == 0) {
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

        values.add(schedule.get(disciplineCipher) == null ? "" : new LinkedHashSet<>(schedule.get(disciplineCipher))
                .stream()
                .map(Schedule::getFacultyAddress)
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

