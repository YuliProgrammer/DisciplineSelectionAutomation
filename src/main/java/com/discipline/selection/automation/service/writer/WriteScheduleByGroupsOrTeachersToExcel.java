package com.discipline.selection.automation.service.writer;

import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.discipline.selection.automation.model.enums.WeekDay.DAYS;
import static com.discipline.selection.automation.model.enums.WeekType.EVERY_WEEK;
import static com.discipline.selection.automation.model.enums.WeekType.WEEK_TYPES;

public abstract class WriteScheduleByGroupsOrTeachersToExcel extends WriteDisciplinesToNewExcel {

    protected List<String> values = new ArrayList<>();
    protected int columnIndex = 0;
    protected int rowIndex = 2;

    /**
     * Void that write schedule to excel.
     *
     * @param sheet - current sheet
     */
    protected void writeSchedule(XSSFSheet sheet) {
        Arrays.stream(DAYS).forEach(day -> {
            IntStream.range(1, 8).forEach(lessonNumber -> {
                Arrays.stream(WEEK_TYPES).forEach(weekType -> {
                    values.add(day.getName());
                    values.add(String.valueOf(lessonNumber));
                    values.add(weekType.getName());
                    values.addAll(getValuesForAll(day, lessonNumber, weekType));
                    writeEntry(sheet, values);
                });
            });
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
    protected List<ScheduleByGroupsOrTeachers> filterSchedule(
            List<Schedule> scheduleByDisciplineForCurrentStudent,
            WeekDay day, int lessonNumber, WeekType weekType) {
        return scheduleByDisciplineForCurrentStudent.stream()
                .filter(schedule -> schedule.getDayOfWeek().equals(day))
                .filter(schedule -> schedule.getLessonNumber().equals(lessonNumber))
                .filter(schedule -> schedule.getTypeOfWeek().equals(weekType) ||
                        (!weekType.equals(EVERY_WEEK) && schedule.getTypeOfWeek().equals(EVERY_WEEK)))
                .map(this::generateScheduleByGroupsOrTeachers)
                .collect(Collectors.toList());
    }

    /**
     * @param schedule - current schedule for particular group or teacher
     * @return - schedule that contains only necessary rows for generating Excel list
     */
    private ScheduleByGroupsOrTeachers generateScheduleByGroupsOrTeachers(Schedule schedule) {
        return ScheduleByGroupsOrTeachers.builder()
                .oneDisciplineCipher(schedule.getDisciplineCipher())
                .facultyType(schedule.getFacultyType())
                .facultyAddress(schedule.getFacultyAddress())
                .dayOfWeek(schedule.getDayOfWeek())
                .lessonNumber(schedule.getLessonNumber())
                .lessonType(schedule.getLessonType())
                .typeOfWeek(schedule.getTypeOfWeek())
                .groupCodes(schedule.getGroupCodes())
                .build();
    }

    private void writeEntry(XSSFSheet sheet, List<String> values) {
        writeEntry(sheet, setForeground(rowIndex), values, rowIndex);
        rowIndex += 1;
        this.values.clear();
    }

    private void writeEmptyLine(XSSFSheet sheet) {
        writeEmptyLine(sheet, emptyCellStyle, rowIndex, columnIndex);
    }

}
