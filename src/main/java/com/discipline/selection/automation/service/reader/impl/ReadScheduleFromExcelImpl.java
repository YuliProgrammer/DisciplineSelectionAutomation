package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.ScheduleMapper;
import com.discipline.selection.automation.model.GroupedSchedule;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.SCHEDULE_FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_SHEET_INDEX;
import static com.discipline.selection.automation.util.Constants.TEACHER;

public class ReadScheduleFromExcelImpl implements ReadFromExcel<String, Map<String, List<Schedule>>> {

    private static String FILE_NAME;

    @Override
    public Map<String, Map<String, List<Schedule>>> uploadData() {
        Map<String, Map<String, List<Schedule>>> groupedSchedulesMap = new HashMap<>();
        Map<String, List<Schedule>> disciplineMap;
        Map<String, List<Schedule>> teacherMap;

        for (String scheduleFiles : SCHEDULE_FILE_NAMES) {
            FILE_NAME = scheduleFiles;
            try (FileInputStream file = new FileInputStream(FILE_NAME)) {
                Workbook workbook = new XSSFWorkbook(file);
                GroupedSchedule groupedSchedule = getSchedule(workbook);

                disciplineMap = groupedSchedulesMap.containsKey(DISCIPLINE) ?
                        groupedSchedulesMap.get(DISCIPLINE) : new HashMap<>();
                teacherMap = groupedSchedulesMap.containsKey(TEACHER) ?
                        groupedSchedulesMap.get(TEACHER) : new HashMap<>();

                disciplineMap.putAll(groupedSchedule.getSchedulesGroupedByDisciplineCipher());

                Map<String, List<Schedule>> schedulesGroupedByTeacher = groupedSchedule.getSchedulesGroupedByTeacher();
                for (Map.Entry<String, List<Schedule>> entry : schedulesGroupedByTeacher.entrySet()) {
                    setScheduleByKey(teacherMap, entry.getKey(), entry.getValue());
                }

                groupedSchedulesMap.put(DISCIPLINE, disciplineMap);
                groupedSchedulesMap.put(TEACHER, teacherMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return groupedSchedulesMap;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return 2 maps:
     * 1. map where key - it is a unique discipline cipher,
     * and value - it is a schedule for this discipline cipher
     * 2. map where key - it is a unique teacher name,
     * and value - it is a schedule for this teacher
     */
    private GroupedSchedule getSchedule(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(SCHEDULE_SHEET_INDEX);
        Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher = new LinkedHashMap<>();
        Map<String, List<Schedule>> schedulesGroupedByTeacher = new LinkedHashMap<>();

        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.isEmpty() || rowData.get(0).isEmpty()) {
                break;
            }

            List<Schedule> schedulesFromCurrentRow =
                    ScheduleMapper.mapRowDataToSchedule(rowData, rowIndex, FILE_NAME);
            String disciplineCipher = schedulesFromCurrentRow.get(0).getDisciplineCipher();
            String teacherName = schedulesFromCurrentRow.get(0).getTeacherName();

            setScheduleByKey(schedulesGroupedByDisciplineCipher, disciplineCipher, schedulesFromCurrentRow);
            setScheduleByKey(schedulesGroupedByTeacher, teacherName, schedulesFromCurrentRow);
        }

        return GroupedSchedule.builder()
                .schedulesGroupedByDisciplineCipher(schedulesGroupedByDisciplineCipher)
                .schedulesGroupedByTeacher(schedulesGroupedByTeacher)
                .build();
    }

    private void setScheduleByKey(Map<String, List<Schedule>> schedulesGroupedByKey, String key,
                                  List<Schedule> schedulesFromCurrentRow) {
        List<Schedule> schedulesByDisciplineCipher = schedulesGroupedByKey.get(key);
        List<Schedule> schedules =
                (schedulesByDisciplineCipher == null) ? new ArrayList<>() : schedulesByDisciplineCipher;

        schedules.addAll(schedulesFromCurrentRow);
        schedulesGroupedByKey.put(key, schedules);
    }
}
