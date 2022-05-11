package com.discipline.selection.automation.service.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Interface that can read data from Excel file
 *
 * @author Yuliia_Dolnikova
 */
public interface ReadFromExcel<K, V> {

    Map<K, V> uploadData();

    /**
     * Void read all cells from the row and save values into Map
     *
     * @param row - current sheet row
     * @return map where key - it is a column index and value - it is a cell data
     */
    default Map<Integer, String> addCellValuesToMap(Row row) {
        if (Objects.isNull(row)) {
            return new HashMap<>();
        }

        String value;
        Map<Integer, String> data = new LinkedHashMap<>();

        for (int columnIndex = 0; columnIndex < row.getLastCellNum(); columnIndex++) {
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                value = "";
            } else {
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    value = String.valueOf((int) cell.getNumericCellValue());
                } else {
                    value = cell.getStringCellValue();
                    value = StringUtils.replace(value, "\u00A0", ""); // trim Excel unprintable symbols
                }
            }
            data.put(columnIndex, value);
        }

        return data;
    }

}
