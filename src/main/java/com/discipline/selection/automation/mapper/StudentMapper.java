package com.discipline.selection.automation.mapper;

import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.discipline.selection.automation.model.Student;

@UtilityClass
public class StudentMapper {

    /**
     * @param rowData - map where key - it is a column index and value - it is column data
     * @return student
     */
    public Student mapRowDataToStudent(Map<Integer, String> rowData) {
        Student student = new Student();

        for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
            String value = entry.getValue().trim();
            switch (entry.getKey()) {
                case 0:
                    student.setFacilityCipher(value);
                    break;
                case 1:
                    student.setEmail(value);
                    break;
                case 2:
                    student.setName(value);
                    break;
                case 3:
                    student.setCourse(value);
                    break;
                case 4:
                    student.setGroup(value.toUpperCase());
                    break;
                case 5:
                    student.setDisciplinesNumber(value);
                    break;
                case 6:
                    student.getDiscipline().setDisciplineCipher(value.toLowerCase());
                    break;
                case 7:
                    student.getDiscipline().setDisciplineName(value);
                    break;
            }
        }

        return student;
    }

    /**
     * @param students - map where key - it is a unique discipline cipher,
     *                 and value - it is a list of all students who have chosen this discipline
     * @return map where key - it is a unique discipline cipher chosen by students from different faculties,
     * and value - it is a list of all students who have chosen this discipline
     */
    public Map<String, List<Student>> getStudentsGroupedByDisciplineCipherForDifferentFacilities(
            Map<String, List<Student>> students) {

        Map<String, List<Student>> studentsGroupedByDisciplineCipherForDifferentFacilities = new LinkedHashMap<>();
        for (Map.Entry<String, List<Student>> entry : students.entrySet()) {
            long facilityCount = entry.getValue().stream().map(Student::getFacilityCipher).distinct().count();
            if (facilityCount > 1) {
                studentsGroupedByDisciplineCipherForDifferentFacilities.put(entry.getKey(), entry.getValue());
            }
        }

        return studentsGroupedByDisciplineCipherForDifferentFacilities;
    }

}
