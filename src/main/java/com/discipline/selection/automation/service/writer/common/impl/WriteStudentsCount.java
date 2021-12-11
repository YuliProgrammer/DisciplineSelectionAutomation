package com.discipline.selection.automation.service.writer.common.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.WriteToExcel;
import com.discipline.selection.automation.service.writer.common.Writer;
import com.discipline.selection.automation.service.writer.existed.impl.WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl;
import com.discipline.selection.automation.service.writer.existed.impl.WriteStudentsCountToExistedExcelSheetImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME;

/**
 * Class that calls of WriteStudentsCountToExistedExcelSheetImpl and WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl
 * in order to optimize saving the result in one excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteStudentsCount implements Writer {

    private final Map<String, List<Student>> students;
    private final Map<String, Discipline> disciplines;

    public WriteStudentsCount(Map<String, List<Student>> students,
                              Map<String, Discipline> disciplines) {
        this.students = students;
        this.disciplines = disciplines;
        calculateCurrentStudentsCountForAllDisciplines(disciplines);
    }

    public void writeToExcel() {
        String fileName = FILE_NAMES.get(0);
        File file = new File(fileName);

        WriteToExcel writeStudentsCount = new WriteStudentsCountToExistedExcelSheetImpl(students, disciplines);
        WriteToExcel writeDisciplinesForDifferentFacilities =
                new WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl(students, disciplines);

        try (FileInputStream inputStream = new FileInputStream(fileName);
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            writeStudentsCount.writeToExcel(workbook);
            System.out.println(
                    String.format(
                            "Поточна кiлькiсть студентiв була записана у iснуючий вхiдний файл \"%s\"(Лист №2).",
                            fileName));
            if (workbook.getSheet(CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME) != null) {
                ((WriteStudentsCountToExistedExcelSheetImpl) writeStudentsCount).setSheetIndex(2);
                writeStudentsCount.writeToExcel(workbook);
                System.out.println(
                        String.format(
                                "Список дисциплiн, якi обрали студенти з рiзних факультетiв, вже iснує у iснуючому вхiдному файлi \"%s\" (Лист №3).",
                                fileName));
            } else {
                writeDisciplinesForDifferentFacilities.writeToExcel(workbook);
                System.out.println(
                        String.format(
                                "Список дисциплiн, якi обрали студенти з рiзних факультетiв, було записано у iснуючий вхiдний файл \"%s\" (Лист №3).",
                                fileName));
            }
            writeToWorkbook(file, workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The void calculates the current number of students for all disciplines.
     *
     * @param disciplines - map where key - it is a unique discipline cipher,
     *                    and value - it is a discipline that match to this discipline cipher
     */
    protected void calculateCurrentStudentsCountForAllDisciplines(Map<String, Discipline> disciplines) {
        for (Map.Entry<String, Discipline> disciplineEntry : disciplines.entrySet()) {
            Discipline discipline = disciplineEntry.getValue();
            List<Student> studentsByDiscipline = students.get(disciplineEntry.getKey());
            int studentsCount = studentsByDiscipline == null ? 0 : studentsByDiscipline.size();
            discipline.setStudentsCount(studentsCount);
            disciplines.put(disciplineEntry.getKey(), discipline);
        }
    }

}
