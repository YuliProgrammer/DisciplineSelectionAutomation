package com.discipline.selection.automation.service.reader.impl;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.DISCIPLINES_SHEET_INDEX;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.discipline.selection.automation.service.reader.ReadFromExcel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.discipline.selection.automation.mapper.DisciplineMapper;
import com.discipline.selection.automation.model.Discipline;

public class ReadDisciplinesFromExcelImpl implements ReadFromExcel<String, Discipline> {

    @Override
    public Map<String, Discipline> uploadData() {
        try (FileInputStream file = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(file);
            return getDisciplines(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return map where key - it is a unique discipline cipher,
     * and value - it is a discipline that match to this discipline cipher
     */
    private Map<String, Discipline> getDisciplines(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(DISCIPLINES_SHEET_INDEX);
        Map<String, Discipline> disciplines = new LinkedHashMap<>();

        for (Row row : sheet) {
            // row with index 0 - it is a table header
            if (row.getRowNum() == 0) {
                continue;
            }

            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.get(0) == null || rowData.get(0).isEmpty()) {
                continue;
            }

            Discipline discipline = DisciplineMapper.mapRowDataToDiscipline(rowData);
            disciplines.put(discipline.getDisciplineCipher(), discipline);
        }

        return disciplines;
    }

}
