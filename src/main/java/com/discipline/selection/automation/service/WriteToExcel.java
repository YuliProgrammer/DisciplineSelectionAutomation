package com.discipline.selection.automation.service;

import com.discipline.selection.automation.mapper.StringMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;

import static com.discipline.selection.automation.util.Constants.SEMICOLON;

/**
 * Interface that can write data to Excel file
 *
 * @author Yuliia_Dolnikova
 */
public interface WriteToExcel {

    void writeToExcel(XSSFWorkbook workbook);

    /**
     * Void that write entry to excel
     *
     * @param sheet     - current sheet
     * @param cellStyle - basic cell style
     * @param values    - the values of current entry fields
     * @param rowIndex  - index of new row
     */
    default void writeEntry(XSSFSheet sheet, CellStyle cellStyle, List<String> values, int rowIndex,
                            CellStyle... problemsCellStyle) {
        Cell cell;
        int columnIndex = 0;
        XSSFRow row = sheet.createRow(rowIndex);

        for (String value : values) {
            Integer intValue = StringMapper.parseStringToInt(value);
            cell = row.createCell(columnIndex++, intValue != null ? CellType.NUMERIC : CellType.STRING);
            cell.setCellValue(value == null || value.isEmpty() ? "" : value);

            if (intValue != null && value != null) {
                cell.setCellValue(intValue);
            }

            if (problemsCellStyle != null && problemsCellStyle.length > 0 && value != null && value.contains(SEMICOLON)) {
                cell.setCellStyle(problemsCellStyle[0]);
            } else {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    /**
     * Void that write empty line to excel
     *
     * @param sheet     - current sheet
     * @param cellStyle - basic cell style
     * @param rowIndex  - index of new row
     */
    default void writeEmptyLine(XSSFSheet sheet, CellStyle cellStyle, int rowIndex, int columnCount) {
        Cell cell;
        int columnIndex = 0;
        XSSFRow row = sheet.createRow(rowIndex);

        for (int i = 0; i < columnCount; i++) {
            cell = row.createCell(columnIndex++, CellType.STRING);
            cell.setCellValue(" ");
            cell.setCellStyle(cellStyle);
        }
    }
}
