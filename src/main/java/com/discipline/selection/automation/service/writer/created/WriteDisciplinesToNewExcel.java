package com.discipline.selection.automation.service.writer.created;

import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.service.writer.WriteToExcel;
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

import static com.discipline.selection.automation.util.Constants.OUTPUT_FILE_NAME;

/**
 * Class that contains a basic logic for writing data into new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public abstract class WriteDisciplinesToNewExcel extends WriteToExcel {

    /**
     * key - discipline cipher, value - schedule for this discipline
     */
    protected Map<String, List<Schedule>> schedules;

    protected WriteDisciplinesToNewExcel(XSSFWorkbook workbook) {
        super(workbook);
    }

    @Override
    public boolean isProcess() {
        return true;
    }

    @Override
    public String getFileName() {
        return OUTPUT_FILE_NAME;
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

}
