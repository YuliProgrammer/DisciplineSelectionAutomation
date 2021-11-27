package com.discipline.selection.automation.service.writer.existed.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Student;
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

import static com.discipline.selection.automation.util.Constants.DISCIPLINES_SHEET_INDEX;
import static com.discipline.selection.automation.util.Constants.STUDENTS_COUNT_COLUMN_TITLE;

/**
 * Class that calculate current students count for all disciplines and
 * writes this count to an existed Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteStudentsCountToExistedExcelSheetImpl extends WriteDisciplinesToExistedExcel {

    public WriteStudentsCountToExistedExcelSheetImpl(Map<String, List<Student>> students,
                                                     Map<String, Discipline> disciplines) {
        this.students = students;
        this.disciplines = disciplines;
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        int indexOfLastColumn = disciplinesHeader.size();
        XSSFSheet sheet = workbook.getSheetAt(DISCIPLINES_SHEET_INDEX);

        writeNewColumnStudentsCountToHeader(sheet, indexOfLastColumn);
        writeCurrentStudentsCountForAllDisciplines(sheet, indexOfLastColumn);
        sheet.autoSizeColumn(indexOfLastColumn);
    }

    /**
     * The void write new column "К-ть студенiв" to the header at the sheet.
     *
     * @param sheet             - current .xlsx sheet
     * @param indexOfLastColumn - index of last column where the header should be written
     */
    private void writeNewColumnStudentsCountToHeader(XSSFSheet sheet, int indexOfLastColumn) {
        XSSFCellStyle cellStyle = CellStyleCreator.createEvenCellStyleCharacteristics(sheet.getWorkbook());
        Cell cell = sheet.getRow(0).createCell(indexOfLastColumn, CellType.STRING);
        cell.setCellValue(STUDENTS_COUNT_COLUMN_TITLE);
        cell.setCellStyle(cellStyle);
    }

    /**
     * The void calculates the current number of students for all disciplines and
     * saves this value in the corresponding line of the worksheet.
     *
     * @param sheet             - current sheet
     * @param indexOfLastColumn - index of last column where the current students count should be written
     */
    private void writeCurrentStudentsCountForAllDisciplines(XSSFSheet sheet, int indexOfLastColumn) {
        int rowIndex = 1;
        for (Map.Entry<String, Discipline> entry : disciplines.entrySet()) {
            XSSFRow currentRow =
                    sheet.getRow(rowIndex) == null ? sheet.createRow(rowIndex++) : sheet.getRow(rowIndex++);
            saveCurrentStudentsCount(currentRow, entry, indexOfLastColumn);
        }
    }

}
