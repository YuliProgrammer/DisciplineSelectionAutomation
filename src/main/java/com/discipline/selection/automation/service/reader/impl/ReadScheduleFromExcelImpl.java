package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.ScheduleMapper;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_SHEET_INDEX;

public class ReadScheduleFromExcelImpl implements ReadFromExcel<String, List<Schedule>> {

    private static String FILE_NAME;

    @Override
    public Map<String, List<Schedule>> uploadData() {
        FILE_NAME = FILE_NAMES.get(1);

        try (FileInputStream file = new FileInputStream(new File(FILE_NAME))) {
            Workbook workbook = new XSSFWorkbook(file);
            return getSchedule(workbook);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return map where key - it is a unique discipline cipher,
     * and value - it is a schedule for this discipline cipher
     */
    private Map<String, List<Schedule>> getSchedule(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(SCHEDULE_SHEET_INDEX);
        Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher = new LinkedHashMap<>();

        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.isEmpty() || rowData.get(0).isEmpty()) {
                break;
            }

            List<Schedule> schedulesFromCurrentRow =
                    ScheduleMapper.mapRowDataToSchedule(rowData, rowIndex, FILE_NAME);
            String disciplineCipher = schedulesFromCurrentRow.get(0).getDisciplineCipher();

            List<Schedule> schedulesByDisciplineCipher = schedulesGroupedByDisciplineCipher.get(disciplineCipher);
            List<Schedule> schedules =
                    (schedulesByDisciplineCipher == null) ? new ArrayList<>() : schedulesByDisciplineCipher;

            schedules.addAll(schedulesFromCurrentRow);

            schedulesGroupedByDisciplineCipher.put(disciplineCipher, schedules);
        }

        return schedulesGroupedByDisciplineCipher;
    }

}
