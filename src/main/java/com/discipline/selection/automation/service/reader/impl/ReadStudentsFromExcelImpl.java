package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.StudentMapper;
import com.discipline.selection.automation.model.GroupedStudents;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.GROUP;
import static com.discipline.selection.automation.util.Constants.STUDENTS_SHEET_INDEX;

public class ReadStudentsFromExcelImpl implements ReadFromExcel<String, Map<String, List<Student>>> {

    @Override
    public Map<String, Map<String, List<Student>>> uploadData() {
        try (FileInputStream file = new FileInputStream(new File(FILE_NAMES.get(0)))) {
            Workbook workbook = new XSSFWorkbook(file);

            GroupedStudents groupedStudents = getStudentsGroupedByDisciplineCipher(workbook);
            Map<String, Map<String, List<Student>>> groupedStudentsMap = new HashMap<>();
            groupedStudentsMap.put(DISCIPLINE, groupedStudents.getStudentsGroupedByDisciplines());
            groupedStudentsMap.put(GROUP, groupedStudents.getStudentsGroupedByGroup());

            return groupedStudentsMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return 2 maps:
     * First: where key - it is a unique discipline cipher
     * and value - it is a list of all students who have chosen this discipline.
     * Second: where key - it's a unique students group and value - it is a list of all students from this group.
     */
    private GroupedStudents getStudentsGroupedByDisciplineCipher(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(STUDENTS_SHEET_INDEX);
        Map<String, List<Student>> studentsGroupedByDisciplines = new LinkedHashMap<>();
        Map<String, List<Student>> studentsGroupedByGroup = new LinkedHashMap<>();

        for (Row row : sheet) {
            // row with index 0 - it is a table header
            if (row.getRowNum() == 0) {
                continue;
            }

            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.isEmpty() || rowData.get(0).isEmpty()) {
                break;
            }

            Student student = StudentMapper.mapRowDataToStudent(rowData);
            String disciplineCipher = student.getDiscipline().getDisciplineCipher().trim();
            String studentGroup = student.getGroup().trim();

            // get students by discipline
            List<Student> valueByDisciplineCipher = studentsGroupedByDisciplines.get(disciplineCipher);
            List<Student> students = (valueByDisciplineCipher == null) ? new ArrayList<>() : valueByDisciplineCipher;
            students.add(student);
            studentsGroupedByDisciplines.put(disciplineCipher, students);

            // get students by group
            List<Student> valueByGroup = studentsGroupedByGroup.get(studentGroup);
            List<Student> studentsByGroups = (valueByGroup == null) ? new ArrayList<>() : valueByGroup;
            studentsByGroups.add(student);
            studentsGroupedByGroup.put(studentGroup, studentsByGroups);
        }

        return GroupedStudents.builder()
                .studentsGroupedByDisciplines(studentsGroupedByDisciplines)
                .studentsGroupedByGroup(studentsGroupedByGroup)
                .build();
    }

}
