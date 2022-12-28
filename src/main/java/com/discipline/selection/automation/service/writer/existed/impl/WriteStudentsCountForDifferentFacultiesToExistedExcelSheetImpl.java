package com.discipline.selection.automation.service.writer.existed.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Student;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME;

/**
 * Class that calculate current students count for all disciplines and
 * writes this count to an existed Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteStudentsCountForDifferentFacultiesToExistedExcelSheetImpl extends WriteStudentsCountToExistedExcelSheetImpl {

    public WriteStudentsCountForDifferentFacultiesToExistedExcelSheetImpl(Map<String, List<Student>> students,
                                                                          Map<String, Discipline> disciplines,
                                                                          Map<Integer, String> disciplinesHeader,
                                                                          XSSFWorkbook workbook) {
        super(students, disciplines, disciplinesHeader, workbook);
    }

    @Override
    public boolean isProcess() {
        if (workbook.getSheet(CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME) != null) {
            System.out.println("К-ть дисциплiн, якi обрали студенти з рiзних факультетiв, вже існує у вхiдному файлі (Лист №3).%n");
            return false;
        }
        return true;
    }

    @Override
    public void writeToExcel(String fileName) {
        System.out.println("\nЗапис к-ть студентів, які обрали дисципліни з різних факультетів...");
        super.setSheetIndex(2);
        super.writeToExcel(FILE_NAME);
        System.out.printf(
                "К-ть студентів, якi обрали дисципліни з рiзних факультетiв, записано у вхiдний файл \"%s\" (Лист №3).%n",
                FILE_NAME);
    }


}
