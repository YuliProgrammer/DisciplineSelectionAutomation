package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import com.discipline.selection.automation.util.Constants;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_SHEET_NAME;

/**
 * Class that creates the consolidation of disciplines and
 * writes this consolidation to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteConsolidationOfDisciplinesToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final Set<String> studentsGroups;

    public WriteConsolidationOfDisciplinesToNewExcelImpl(Map<String, List<Student>> students,
                                                         Map<String, Discipline> disciplines,
                                                         Map<String, List<Schedule>> schedules) {
        this.students = students;
        this.disciplines = disciplines;
        this.schedules = schedules;
        this.studentsGroups = getStudentsGroups();
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);
        XSSFSheet sheet = workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_SHEET_NAME);

        writeHeader(sheet);
        writeDiscipline(sheet);
    }

    /**
     * Void that write header for "Зведення дисц шифр спец".
     * That header consists of two parts: the first part - it is basic header titles and
     * the second part - it is a first 2 letters of students classes.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        int columnIndex = 0;
        Set<String> consolidationOfDisciplines = Constants.CONSOLIDATION_OF_DISCIPLINES_HEADER;
        columnIndex = writeHeader(sheet, consolidationOfDisciplines, columnIndex); // write the first part of the header

        writeHeader(sheet, studentsGroups, columnIndex); // write the second part of the header
    }

    /**
     * Void that write disciplines to excel.
     *
     * @param sheet - current sheet
     */
    private void writeDiscipline(XSSFSheet sheet) {
        int rowIndex = 2;
        for (Discipline discipline : disciplines.values()) {
            List<Student> studentsByDiscipline = students.get(discipline.getDisciplineCipher());

            discipline.setStudentsCount(studentsByDiscipline.size());
            discipline.setStudentsCountByGroups(getStudentsCountByGroups(studentsByDiscipline));
            discipline.setSchedule(schedules);

            writeEntry(sheet, setForeground(rowIndex), discipline.getValuesForConsolidationOfDiscipline(), rowIndex);
            rowIndex++;
        }
    }

    /**
     * @return set of unique first 2 letters of students groups
     */
    private Set<String> getStudentsGroups() {
        Set<String> studentsGroups = new LinkedHashSet<>();
        students.forEach((key, value) -> value.forEach(student ->
                studentsGroups
                        .add(student.getGroup().length() < 2 ? student.getGroup() : student.getGroup().substring(0, 2))
        ));
        return studentsGroups;
    }

    /**
     * @param studentsByDiscipline - list of students who chose same discipline
     * @return list of students count
     */
    private List<Long> getStudentsCountByGroups(List<Student> studentsByDiscipline) {
        return studentsGroups.stream()
                .map(group -> studentsByDiscipline
                        .stream()
                        .filter(student -> student.getGroup().startsWith(group))
                        .count())
                .collect(Collectors.toList());
    }

}
