package com.discipline.selection.automation.service.writer;

import com.discipline.selection.automation.mapper.StringMapper;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.dto.CellStyles;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.EMPTY_VALUE;
import static com.discipline.selection.automation.util.Constants.SEMICOLON;

/**
 * Interface that can write data to Excel file
 *
 * @author Yuliia_Dolnikova
 */
public abstract class WriteToExcel implements WriterChain {

    /**
     * key - discipline cipher, value - list of students who's chosen this discipline
     */
    protected Map<String, List<Student>> students;

    /**
     * key - discipline cipher, value - discipline
     */
    protected Map<String, Discipline> disciplines;

    protected WriterChain nextWriter;
    protected CellStyles cellStyles;
    protected XSSFWorkbook workbook;

    protected WriteToExcel(XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.cellStyles = CellStyles.getInstance(workbook);
    }

    @Override
    public void setNextWriter(WriterChain nextWriter) {
        this.nextWriter = nextWriter;
    }

    @Override
    public void execute() {
        if (isProcess()) {
            try {
                String fileName = getFileName().equals(FILE_NAME) ? FILE_NAME : getCommonFileName() + getFileName();
                writeToExcel(fileName);
                writeToWorkbook(new File(fileName), workbook);
            } catch (Exception e) {
                System.err.println("Неможливо записати в лист через: " + e);
            }
        }

        if (nextWriter != null) {
            nextWriter.execute();
        } else {
            closeWorkbook(workbook);
        }
    }

    /**
     * The method generates a common file name for the current system
     *
     * @return common file name
     */
    private String getCommonFileName() {
        String separator = File.separator;
        String inputFileName = FILE_NAME;
        int indexOfLastSlash = inputFileName.contains(separator) ? inputFileName.lastIndexOf(separator) + 1 : 0;

        String outputPath = inputFileName.substring(0, indexOfLastSlash);

        String outputYear = getYear();
        return outputPath + outputYear;
    }

    /**
     * If the current date after January 1 and before September 10 - @return current year.
     * In other cases - @return next year.
     */
    private String getYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        LocalDate minCurrentDate = LocalDate.of(currentYear, 1, 1);  // January 1
        LocalDate maxCurrentDate = LocalDate.of(currentYear, 9, 10); // September 10

        return currentDate.isAfter(minCurrentDate) && currentDate.isBefore(maxCurrentDate) ?
                String.valueOf(currentYear) : String.valueOf(currentYear + 1);
    }

    /**
     * Void closes the passed workbook
     *
     * @param workbook - current workbook
     */
    private void closeWorkbook(XSSFWorkbook workbook) {
        try {
            workbook.close();
        } catch (IOException e) {
            System.err.println("Неможливо закрити документ через: " + e);
        }
    }

    /**
     * Void writes entry to excel
     *
     * @param sheet     - current sheet
     * @param cellStyle - basic cell style
     * @param values    - the values of current entry fields
     * @param rowIndex  - index of new row
     */
    protected void writeEntry(XSSFSheet sheet, CellStyle cellStyle, List<String> values, int rowIndex,
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

            if (problemsCellStyle != null && problemsCellStyle.length > 0
                    && value != null && (value.contains(SEMICOLON) || value.equals(EMPTY_VALUE))) {
                cell.setCellStyle(problemsCellStyle[0]);
            } else {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    /**
     * Void writes empty line to excel
     *
     * @param sheet     - current sheet
     * @param cellStyle - basic cell style
     * @param rowIndex  - index of new row
     */
    protected void writeEmptyLine(XSSFSheet sheet, CellStyle cellStyle, int rowIndex, int columnCount) {
        Cell cell;
        int columnIndex = 0;
        XSSFRow row = sheet.createRow(rowIndex);

        for (int i = 0; i < columnCount; i++) {
            cell = row.createCell(columnIndex++, CellType.STRING);
            cell.setCellValue(" ");
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * @param rowIndex - index of current row for identification this rows like odd or even.
     * @return cell style.
     */
    protected XSSFCellStyle setForeground(int rowIndex) {
        if (rowIndex % 2 == 0) {
            return cellStyles.getEvenCellStyle();
        } else {
            return cellStyles.getOddCellStyle();
        }
    }

}
