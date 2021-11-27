package com.discipline.selection.automation.service.writer.existed;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.WriteToExcel;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import com.discipline.selection.automation.service.reader.impl.ReadDisciplinesHeaderFromExcelImpl;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.util.Constants.STUDENTS_COUNT_COLUMN_TITLE;

/**
 * Class that contains a basic logic for writing data into existed Excel file
 *
 * @author Yuliia_Dolnikova
 */
public abstract class WriteDisciplinesToExistedExcel implements WriteToExcel {

    private final ReadFromExcel<Integer, String> readDisciplinesHeaderFromExcel =
            new ReadDisciplinesHeaderFromExcelImpl();

    protected XSSFCellStyle evenCellStyle;
    protected XSSFCellStyle oddCellStyle;
    protected Map<String, List<Student>> students;
    protected Map<String, Discipline> disciplines;
    protected Map<Integer, String> disciplinesHeader = readDisciplinesHeaderFromExcel.uploadData();

    /**
     * The void saves the current students count in the corresponding line of the worksheet.
     *
     * @param currentRow        - current row where new cell should be created.
     * @param entry             - current discipline for which the current number of students need to be calculated.
     * @param indexOfLastColumn - index of last column where the current students count was written.
     */
    protected void saveCurrentStudentsCount(XSSFRow currentRow, Map.Entry<String, Discipline> entry,
                                            int indexOfLastColumn) {
        Integer maxStudentsCount = entry.getValue().getMaxStudentsCount();
        Integer studentsCount = entry.getValue().getStudentsCount();

        Cell newCell = currentRow.createCell(indexOfLastColumn, CellType.NUMERIC);
        newCell.setCellValue(studentsCount);

        applyStyleToRow(currentRow, studentsCount, maxStudentsCount);
    }

    /**
     * The void that apply CellStyle for all cells in current row.
     *
     * @param currentRow       - current row to apply styles
     * @param studentsCount    - count of students who have chosen current discipline
     * @param maxStudentsCount - maximum possible students count for current discipline
     */
    private void applyStyleToRow(XSSFRow currentRow, Integer studentsCount, Integer maxStudentsCount) {
        XSSFCellStyle foregroundStyle = addForegroundDependingOnTheStudentsCount(studentsCount, maxStudentsCount,
                currentRow.getRowNum(), currentRow.getSheet().getWorkbook());
        for (Cell cell : currentRow) {
            cell.setCellStyle(foregroundStyle);
        }
    }

    /**
     * The void that create CellStyle depending on current and maximum students count.
     * If the current students count more than the maximum possible students count, than the foreground of this row should be red.
     * If the current students count equals to the maximum possible students count, than the foreground of this row should be orange.
     * If the current students count less than the maximum possible students count by no more than 25% inclusive,
     * than the foreground of this row should be yellow.
     * In other cases the foreground should be white.
     *
     * @param studentsCount    - count of students who have chosen current discipline
     * @param maxStudentsCount - maximum possible students count for current discipline
     * @param rowIndex         - index of current row for identification this rows like odd or even
     * @param workbook         - current workbook
     * @return style of new cell that contains studentsCount
     */
    private XSSFCellStyle addForegroundDependingOnTheStudentsCount(Integer studentsCount, Integer maxStudentsCount,
                                                                   int rowIndex, XSSFWorkbook workbook) {
        XSSFCellStyle cellStyle = CellStyleCreator.createEvenCellStyleCharacteristics(workbook);

        if (maxStudentsCount == null) {
            return setForeground(rowIndex);
        }

        if (studentsCount.equals(maxStudentsCount)) {
            cellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        } else if (studentsCount > maxStudentsCount) {
            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
        } else if (maxStudentsCount - studentsCount <= maxStudentsCount * 0.25) {
            cellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        }

        return cellStyle;
    }

    /**
     * @param rowIndex - index of current row for identification this rows like odd or even
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
