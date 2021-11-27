package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.service.reader.ReadFromExcel;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.DISCIPLINES_SHEET_INDEX;

public class ReadDisciplinesHeaderFromExcelImpl implements ReadFromExcel<Integer, String> {

    @Override
    public Map<Integer, String> uploadData() {
        try (FileInputStream file = new FileInputStream(new File(FILE_NAMES.get(0)))) {
            Workbook workbook = new XSSFWorkbook(file);
            return getDisciplinesHeader(workbook);
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
