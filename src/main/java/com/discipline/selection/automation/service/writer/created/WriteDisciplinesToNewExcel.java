package com.discipline.selection.automation.service.writer.created;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.WriteToExcel;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class that contains a basic logic for writing data into new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public abstract class WriteDisciplinesToNewExcel implements WriteToExcel {

    protected XSSFCellStyle emptyCellStyle;
    protected XSSFCellStyle evenCellStyle;
    protected XSSFCellStyle oddCellStyle;
    protected XSSFCellStyle farCellStyle;
    protected XSSFCellStyle duplicatedCellStyle;
    protected XSSFCellStyle duplicatedAndFarCellStyle;

    protected Map<String, List<Student>> students;
    protected Map<String, Discipline> disciplines;
    protected Map<String, List<Schedule>> schedules;

    /**
     * Void initialize cell styles
     *
     * @param workbook - current workbook
     */
    protected void initStyles(XSSFWorkbook workbook) {
        emptyCellStyle = CellStyleCreator.createDefaultCellStyleCharacteristics(workbook);
        evenCellStyle = CellStyleCreator.createEvenCellStyleCharacteristics(workbook);
        oddCellStyle = CellStyleCreator.createOddCellStyleCharacteristics(workbook);
        farCellStyle = CellStyleCreator.createFarCellStyleCharacteristics(workbook);
        duplicatedCellStyle = CellStyleCreator.createDuplicatedCellStyleCharacteristics(workbook);
        duplicatedAndFarCellStyle = CellStyleCreator.createDuplicatedFarCellStyleCharacteristics(workbook);
    }

    /**
     * Void that write headers: the first header - it is a titles' values and
     * the second header - it is a column index that starts form 1.
     *
     * @param sheet       - current new sheet
     * @param titles      - list of header titles
     * @param columnIndex - the index of the first column from which the headers should be written
     * @return index of last column that contains title
     */
    protected int writeHeader(XSSFSheet sheet, Collection<String> titles, int columnIndex) {
        Cell stringFirstTitleCell;
        Cell columnIndexSecondTitleCell;

        XSSFCellStyle firstHeader = CellStyleCreator.createMainHeaderCellStyleCharacteristics(sheet.getWorkbook());
        XSSFCellStyle secondHeader =
                CellStyleCreator.createAdditionalHeaderCellStyleCharacteristics(sheet.getWorkbook());

        Row firstRow = sheet.getRow(0) == null ? sheet.createRow(0) : sheet.getRow(0);
        Row secondRow = sheet.getRow(1) == null ? sheet.createRow(1) : sheet.getRow(1);

        for (String title : titles) {
            stringFirstTitleCell = firstRow.createCell(columnIndex, CellType.STRING);
            columnIndexSecondTitleCell = secondRow.createCell(columnIndex, CellType.NUMERIC);

            stringFirstTitleCell.setCellValue(title);
            stringFirstTitleCell.setCellStyle(firstHeader);

            columnIndexSecondTitleCell.setCellValue(columnIndex + 1);
            columnIndexSecondTitleCell.setCellStyle(secondHeader);

            if (title.contains("НПП") || title.contains("Назва") || title.contains("iм'я")) {
                sheet.setColumnWidth(columnIndex, 45 * 256);
            } else if (title.contains("Шифр") || title.contains("дублiкат") || title.matches(".*-.*-.*")
                    || title.matches(".*\\.[а-яА-Я]{1}\\.")) {
                sheet.setColumnWidth(columnIndex, 15 * 256);
            } else {
                sheet.setColumnWidth(columnIndex, 9 * 256);
            }

            columnIndex++;
        }

        return columnIndex;
    }

    /**
     * @param rowIndex - index of current row for identification this rows like odd or even.
     * @return cell style.
     */
    protected XSSFCellStyle setForeground(int rowIndex) {
        if (rowIndex % 2 == 0) {
            return evenCellStyle;
        } else {
            return oddCellStyle;
        }
    }

}
