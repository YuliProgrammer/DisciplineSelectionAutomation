package com.discipline.selection.automation.service.writer.created;

import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.discipline.selection.automation.model.enums.WeekDay.DAYS;
import static com.discipline.selection.automation.model.enums.WeekType.EVERY_WEEK;
import static com.discipline.selection.automation.model.enums.WeekType.WEEK_TYPES;
import static com.discipline.selection.automation.util.Constants.OUTPUT_FILE_NAME_SCHEDULE;

public abstract class WriteScheduleForAllWorkDaysToExcel extends WriteDisciplinesToNewExcel {

    protected List<String> values = new ArrayList<>();
    protected int columnIndex = 0;
    protected int rowIndex = 2;

    protected WriteScheduleForAllWorkDaysToExcel(XSSFWorkbook workbook) {
        super(workbook);
    }

    @Override
    public String getFileName() {
        return OUTPUT_FILE_NAME_SCHEDULE;
    }

    /**
     * Void that write schedule to excel.
     *
     * @param sheet - current sheet
     */
    protected void writeSchedule(XSSFSheet sheet, CellStyle... duplicatedCellStyle) {
        Arrays.stream(DAYS).forEach(day -> {
            IntStream.range(1, 8).forEach(lessonNumber ->
                    Arrays.stream(WEEK_TYPES).forEach(weekType -> {
                        values.add(day.getName());
                        values.add(String.valueOf(lessonNumber));
                        values.add(weekType.getName());
                        values.addAll(getValuesForAll(day, lessonNumber, weekType));
                        writeEntry(sheet, values, duplicatedCellStyle);
                    })
            );
            writeEmptyLine(sheet);
        });
    }

    protected abstract List<String> getValuesForAll(WeekDay day, int lessonNumber, WeekType weekType);

    /**
     * @param scheduleByDisciplineForCurrentStudent - list of schedules for current student
     * @param day                                   - the day of week on which the discipline is to be held
     * @param lessonNumber                          - the lesson number on which the discipline is to be held
     * @param weekType                              - the week type on which the discipline is to be held
     * @return list of schedules for current student that that corresponds to current parameters
     */
    protected List<Schedule> filterSchedule(
            List<Schedule> scheduleByDisciplineForCurrentStudent,
            WeekDay day, int lessonNumber, WeekType weekType) {
        return scheduleByDisciplineForCurrentStudent.stream()
                .filter(schedule -> schedule.getScheduleDate().getDayOfWeek().equals(day))
                .filter(schedule -> schedule.getScheduleDate().getLessonNumber().equals(lessonNumber))
                .filter(schedule -> schedule.getScheduleDate().getTypeOfWeek().equals(weekType) ||
                        (!weekType.equals(EVERY_WEEK) && schedule.getScheduleDate().getTypeOfWeek().equals(EVERY_WEEK)))
                .collect(Collectors.toList());
    }

    /**
     * @param scheduleByDisciplineForCurrentStudent - list of schedules for current student
     * @return list of ScheduleByGroupsOrTeachers
     */
    protected List<ScheduleByGroupsOrTeachers> mapScheduleToScheduleByGroupsOrTeachers(
            List<Schedule> scheduleByDisciplineForCurrentStudent) {
        return scheduleByDisciplineForCurrentStudent.stream()
                .map(this::generateScheduleByGroupsOrTeachers)
                .collect(Collectors.toList());
    }

    /**
     * @param schedule - current schedule for particular group or teacher
     * @return - schedule that contains only necessary rows for generating Excel list
     */
    private ScheduleByGroupsOrTeachers generateScheduleByGroupsOrTeachers(Schedule schedule) {
        return ScheduleByGroupsOrTeachers.builder()
                .oneDisciplineCipher(schedule.getDiscipline().getDisciplineCipher())
                .facultyType(schedule.getFacultyType())
                .facultyAddress(schedule.getFacultyAddress())
                .dayOfWeek(schedule.getScheduleDate().getDayOfWeek())
                .lessonNumber(schedule.getScheduleDate().getLessonNumber())
                .lessonType(schedule.getLessonType())
                .typeOfWeek(schedule.getScheduleDate().getTypeOfWeek())
                .fileName(schedule.getFileName())
                .groupCodes(schedule.getGroupSchedule().stream()
                        .map(groupSchedule -> groupSchedule.getGroup().getGroupCode())
                        .collect(Collectors.toList()))
                .build();
    }

    private void writeEntry(XSSFSheet sheet, List<String> values, CellStyle... duplicatedCellStyle) {
        writeEntry(sheet, setForeground(rowIndex), values, rowIndex, duplicatedCellStyle);
        rowIndex += 1;
        this.values.clear();
    }

    private void writeEmptyLine(XSSFSheet sheet) {
        writeEmptyLine(sheet, cellStyles.getEmptyCellStyle(), rowIndex, columnIndex);
    }

}
