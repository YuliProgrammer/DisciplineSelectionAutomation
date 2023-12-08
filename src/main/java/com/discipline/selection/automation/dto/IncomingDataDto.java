package com.discipline.selection.automation.dto;

import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.Student;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IncomingDataDto {

    /**
     * Key - discipline cipher, value - concrete discipline
     */
    private Map<String, Discipline> disciplines;

    /**
     * Key - column number, value - column value
     */
    private Map<Integer, String> disciplineHeader;

    /**
     * Key - discipline cipher, value - schedule for this discipline
     */
    private Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher;

    /**
     * Key - teacher name, value - schedule for this teacher
     */
    private Map<String, List<Schedule>> schedulesGroupedByTeacher;

    /**
     * Key - discipline cipher, value - students, who's chosen this discipline
     */
    private Map<String, List<Student>> studentsGroupedByDiscipline;

    /**
     * Key - group, value - students from this group
     */
    private Map<String, List<Student>> studentsGroupedByGroup;

}
