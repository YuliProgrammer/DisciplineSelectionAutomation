package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.StudentMapper;
import com.discipline.selection.automation.model.GroupedStudents;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Student;
import com.discipline.selection.automation.repository.StudentRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.GROUP;
import static com.discipline.selection.automation.util.Constants.STUDENTS_SHEET_INDEX;

@Service
@AllArgsConstructor
public class ReadStudentsFromExcelImpl extends BasicExcelReaderChain<String, Map<String, List<Student>>> {

    private final StudentRepository studentRepository;

    @Override
    public Map<String, Map<String, List<Student>>> uploadData() {
        try (FileInputStream file = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(file);

            GroupedStudents groupedStudents = getStudentsGroupedByDisciplineCipher(workbook);
            Map<String, Map<String, List<Student>>> groupedStudentsMap = new HashMap<>();
            groupedStudentsMap.put(DISCIPLINE, groupedStudents.getStudentsGroupedByDisciplines());
            groupedStudentsMap.put(GROUP, groupedStudents.getStudentsGroupedByGroup());

            incomingDataDto.setStudentsGroupedByDiscipline(groupedStudents.getStudentsGroupedByDisciplines());
            incomingDataDto.setStudentsGroupedByGroup(groupedStudents.getStudentsGroupedByGroup());

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
        List<Student> students = readAllStudents(workbook);
        students = combineStudentsWithTheSameEmail(students);
        System.out.println("\nСтудентів було успішно зчитано з файлу");

        studentRepository.saveAll(students);
        System.out.println("Студентів було успішно збережено в базі даних");

        Map<String, List<Student>> studentsGroupedByDisciplines = new LinkedHashMap<>();
        Map<String, List<Student>> studentsGroupedByGroup = new LinkedHashMap<>();
        students.forEach(student -> {
            // get students by discipline
            student.getDisciplines().stream().filter(Objects::nonNull).forEach(discipline -> {
                String disciplineCipher = discipline.getDisciplineCipher();
                List<Student> valueByDisciplineCipher = studentsGroupedByDisciplines.get(disciplineCipher);
                List<Student> studentsByDisciplineCipher = (valueByDisciplineCipher == null) ? new ArrayList<>() : valueByDisciplineCipher;
                studentsByDisciplineCipher.add(student);
                studentsGroupedByDisciplines.put(disciplineCipher, studentsByDisciplineCipher);
            });

            // get students by group
            String studentGroup = student.getGroup().getGroupCode();
            List<Student> valueByGroup = studentsGroupedByGroup.get(studentGroup);
            List<Student> studentsByGroups = (valueByGroup == null) ? new ArrayList<>() : valueByGroup;
            studentsByGroups.add(student);
            studentsGroupedByGroup.put(studentGroup, studentsByGroups);
        });

        return GroupedStudents.builder()
                .studentsGroupedByDisciplines(studentsGroupedByDisciplines)
                .studentsGroupedByGroup(studentsGroupedByGroup)
                .build();
    }

    /**
     * Method reads Excel sheet, where each row represents a concrete discipline chosen by a student.
     *
     * @param workbook - input .xlsx workbook
     * @return list that contains multiple elements with the same student email but different disciplines
     * <p>
     * <u>Example of output:</u>
     * <p>[{"email": "alex@gmail.com", "disciplines": [{"A"}]}, {"email": "alex@gmail.com", "disciplines": [{"B"}]}]
     */
    private List<Student> readAllStudents(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(STUDENTS_SHEET_INDEX);
        List<Student> students = new ArrayList<>();
        for (Row row : sheet) {
            // row with index 0 - it is a table header
            if (row.getRowNum() == 0) {
                continue;
            }
            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.isEmpty() || rowData.get(0).isEmpty()) {
                break;
            }
            students.add(StudentMapper.mapRowDataToStudent(rowData));
        }
        return students;
    }

    /**
     * Method combines elements with the same email from the {@param students} into one element
     * by joining student's disciplines into one set. It also replace short description of discipline (cipher and name)
     * by full information about it.
     * <p>
     * <u>Example:</u>
     * <p>Input: [{"email": "alex@gmail.com", "disciplines": [{"A"}]}, {"email": "alex@gmail.com", "disciplines": [{"B"}]}]</p>
     * <p>Output: [{"email": "alex@gmail.com", "disciplines": [{"A"}, {"B"}]}]</p>
     *
     * @param students - list of students where each element represent a student with only 1 discipline
     * @return list of unique students where each student has a list of disciplines
     */
    private static List<Student> combineStudentsWithTheSameEmail(List<Student> students) {
        Map<String, List<Student>> studentsByEmail = students.stream().collect(Collectors.groupingBy(Student::getEmail));
        students = studentsByEmail.values().stream().map(studentsToCombine -> {
            Student student = studentsToCombine.get(0);
            // replace discipline object which contains only cipher and name by object
            // which contains all information about discipline
            Set<Discipline> disciplines = studentsToCombine.stream()
                    .flatMap(st -> st.getDisciplines().stream())
                    .map(discipline -> incomingDataDto.getDisciplines().get(discipline.getDisciplineCipher()))
                    .collect(Collectors.toSet());
            student.setDisciplines(disciplines);
            return student;
        }).collect(Collectors.toList());
        return students;
    }

}
