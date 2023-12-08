package com.discipline.selection.automation.service.writer.existed.impl;

import com.discipline.selection.automation.mapper.StudentMapper;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Student;
import com.discipline.selection.automation.service.writer.existed.WriteDisciplinesToExistedExcel;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.STUDENTS_COUNT_COLUMN_TITLE;

/**
 * Class that finds the disciplines selected by students from different faculties and
 * writes this disciplines to an existed Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl extends WriteDisciplinesToExistedExcel {


    public WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl(Map<String, List<Student>> students,
                                                                    Map<String, Discipline> disciplines,
                                                                    Map<Integer, String> disciplinesHeader,
                                                                    XSSFWorkbook workbook) {
        super(disciplinesHeader, workbook);
        this.students = StudentMapper.getStudentsGroupedByDisciplineCipherForDifferentFacilities(students);
        this.disciplines = disciplines.entrySet().stream()
                .filter(entry -> this.students.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.workbook = workbook;
    }

    @Override
    public boolean isProcess() {
        if (workbook.getSheet(CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME) != null) {
            System.out.println("Список дисциплiн, якi обрали студенти з рiзних факультетiв, вже існує у вхiдному файлі (Лист №3).%n");
            return false;
        }
        return true;
    }

    @Override
    public void writeToExcel(String fileName) {
        System.out.println("\nЗапис дисциплін, які обрали студенти різних факультетів...");

        XSSFSheet sheet = workbook.createSheet(CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME);
        int indexOfLastColumn = writeHeader(sheet);
        writeDisciplines(sheet, indexOfLastColumn);

        sheet.autoSizeColumn(indexOfLastColumn);

        System.out.printf(
                "Список дисциплiн, якi обрали студенти з рiзних факультетiв, було записано у iснуючий вхiдний файл \"%s\" (Лист №3).%n",
                fileName);
    }

    /**
     * The void write header to the sheet.
     *
     * @param sheet - current sheet
     * @return index of last column that contains current students count.
     */
    private int writeHeader(XSSFSheet sheet) {
        XSSFCellStyle headerCellStyle = CellStyleCreator.createMainHeaderCellStyleCharacteristics(sheet.getWorkbook());
        int indexOfLastColumn = disciplinesHeader.size();
        XSSFRow firstRow = sheet.createRow(0);
        Cell cell;

        disciplinesHeader.put(indexOfLastColumn, STUDENTS_COUNT_COLUMN_TITLE);
        for (Map.Entry<Integer, String> entry : disciplinesHeader.entrySet()) {
            cell = firstRow.createCell(entry.getKey(), CellType.STRING);
            cell.setCellValue(entry.getValue());
            cell.setCellStyle(headerCellStyle);
        }

        return indexOfLastColumn;
    }

    /**
     * The void calculates the current number of students for all disciplines and
     * saves this value in the corresponding line of the worksheet.
     *
     * @param sheet             - current sheet
     * @param indexOfLastColumn - index of last column where the current students count should be written
     */
    private void writeDisciplines(XSSFSheet sheet, int indexOfLastColumn) {
        int rowIndex = 1;
        for (Map.Entry<String, Discipline> entry : disciplines.entrySet()) {
            List<String> disciplineValues = entry.getValue().getValuesForChosenDiscipline();
            writeEntry(sheet, setForeground(rowIndex), disciplineValues, rowIndex);

            XSSFRow currentRow = sheet.getRow(rowIndex++);
            saveCurrentStudentsCount(currentRow, entry, indexOfLastColumn);
        }
    }

}
