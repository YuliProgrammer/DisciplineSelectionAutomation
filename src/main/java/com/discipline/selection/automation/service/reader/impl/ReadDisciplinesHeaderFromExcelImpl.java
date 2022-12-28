package com.discipline.selection.automation.service.reader.impl;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.DISCIPLINES_SHEET_INDEX;

public class ReadDisciplinesHeaderFromExcelImpl extends BasicExcelReaderChain<Integer, String> {

    @Override
    public Map<Integer, String> uploadData() {
        try (FileInputStream file = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(file);

            Map<Integer, String> disciplinesHeader = getDisciplinesHeader(workbook);
            incomingDataDto.setDisciplineHeader(disciplinesHeader);
            return disciplinesHeader;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return map where key - it is a column index, and value - it is a cell value.
     */
    private Map<Integer, String> getDisciplinesHeader(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(DISCIPLINES_SHEET_INDEX);
        return sheet.getRow(0) == null ? new LinkedHashMap<>() : addCellValuesToMap(sheet.getRow(0));
    }

}
