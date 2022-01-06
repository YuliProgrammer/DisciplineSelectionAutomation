package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.ScheduleByTeachers;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.discipline.selection.automation.model.enums.WeekDay.DAYS;
import static com.discipline.selection.automation.model.enums.WeekType.EVERY_WEEK;
import static com.discipline.selection.automation.model.enums.WeekType.WEEK_TYPES;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByTeachersToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final Set<String> teacherNames = new LinkedHashSet<>();
    private int rowIndex = 2;
    private int columnIndex = 0;
    private List<String> values = new ArrayList<>();

    public WriteScheduleByTeachersToNewExcelImpl(Map<String, List<Schedule>> schedulesGroupedByTeacher,
                                                 Map<String, Discipline> disciplines) {
        this.schedules = schedulesGroupedByTeacher;
        this.disciplines = disciplines;
        this.teacherNames.addAll(getTeacherNames());
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);
        XSSFSheet scheduleSheet = workbook.createSheet(SCHEDULE_BY_TEACHER_SHEET_NAME);
        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet);
    }


    /**
     * Void that write header for "Розклад НПП".
     * That header consists of two parts: the first part - it is basic header titles and
     * the second part - it is a first 2 letters of students classes.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER, columnIndex);
        Set<String> header = new LinkedHashSet<>();
        teacherNames.forEach(teacherName -> {
            header.add(teacherName);
            header.addAll(SCHEDULE_BY_TEACHER_HEADER);
        });
        columnIndex = writeHeader(sheet, header, columnIndex); // write the second part of the header
    }


    /**
     * Void that write schedule to excel.
     *
     * @param sheet - current sheet
     */
    private void writeSchedule(XSSFSheet sheet) {
        Arrays.stream(DAYS).forEach(day -> {
            IntStream.range(1, 8).forEach(lessonNumber -> {
                Arrays.stream(WEEK_TYPES).forEach(weekType -> {
                    values.add(day.getName());
                    values.add(String.valueOf(lessonNumber));
                    values.add(weekType.getName());
                    values.addAll(getValuesForAllTeachers(day, lessonNumber, weekType));
                    writeEntry(sheet, values);
                });
            });
            writeEmptyLine(sheet);
        });
    }

    /**
     * Method get schedules  for all teachers that corresponds to current parameters
     *
     * @param day          - the day of week on which the discipline is to be held
     * @param lessonNumber - the lesson number on which the discipline is to be held
     * @param weekType     - the week type on which the discipline is to be held
     * @return list of discipline ciphers. Each element - it is an enumeration of disciplines for one teacher
     */
    private List<String> getValuesForAllTeachers(WeekDay day, int lessonNumber, WeekType weekType) {
        List<String> teacherDisciplines = new ArrayList<>();
        for (String teacherName : teacherNames) {
            List<Schedule> scheduleByCurrentTeacher = schedules.get(teacherName);
            Set<ScheduleByGroupsOrTeachers> scheduleByTeacher =
                    new HashSet<>(filterScheduleForTeacher(scheduleByCurrentTeacher, day, lessonNumber, weekType));
            Set<ScheduleByTeachers> disciplineValues = getDisciplinesForAllTeachersAndOneLesson(scheduleByTeacher);
            teacherDisciplines.add(disciplineValues.stream()
                    .map(ScheduleByTeachers::getDisciplineCipher)
                    .collect(Collectors.joining()));
            teacherDisciplines.add(disciplineValues.stream()
                    .map(ScheduleByTeachers::getFacultyAddress)
                    .collect(Collectors.joining()));

            // TODO
            teacherDisciplines.add("File name");
        }

        return teacherDisciplines.stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * @param scheduleByTeacher - list of schedules for current student
     * @param day               - the day of week on which the discipline is to be held
     * @param lessonNumber      - the lesson number on which the discipline is to be held
     * @param weekType          - the week type on which the discipline is to be held
     * @return list of schedules for current student that that corresponds to current parameters
     */
    private List<ScheduleByGroupsOrTeachers> filterScheduleForTeacher(List<Schedule> scheduleByTeacher,
                                                                      WeekDay day, int lessonNumber,
                                                                      WeekType weekType) {
        return scheduleByTeacher.stream()
                .filter(schedule -> schedule.getDayOfWeek().equals(day))
                .filter(schedule -> schedule.getLessonNumber().equals(lessonNumber))
                .filter(schedule -> schedule.getTypeOfWeek().equals(weekType) ||
                        (!weekType.equals(EVERY_WEEK) && schedule.getTypeOfWeek().equals(EVERY_WEEK)))
                .map(this::generateScheduleByTeacher)
                .collect(Collectors.toList());
    }

    /**
     * @param schedule - current schedule
     * @param group    - current group
     * @return true when current group starts with one of group code from the current schedule
     */
    private boolean scheduleContainsGroup(Schedule schedule, String group) {
        return schedule.getGroupCodes().stream().anyMatch(group::startsWith);
    }

    /**
     * @param scheduleByTeachers - list of schedules for current teacher
     * @return set of disciplines ciphers for current teacher
     */
    private Set<ScheduleByTeachers> getDisciplinesForAllTeachersAndOneLesson(
            Set<ScheduleByGroupsOrTeachers> scheduleByTeachers) {
        Set<ScheduleByTeachers> disciplineValues = new HashSet<>();
        scheduleByTeachers.forEach(schedule -> {
            disciplineValues.add(ScheduleByTeachers.builder()
                    .disciplineCipher(schedule.getOneDisciplineCipher() + "\n")
                    .facultyAddress(schedule.getFacultyAddress() + "\n")
                    .build());
        });
        return disciplineValues;
    }

    private void writeEntry(XSSFSheet sheet, List<String> values) {
        writeEntry(sheet, setForeground(rowIndex), values, rowIndex);
        rowIndex += 1;
        this.values = new ArrayList<>();
    }

    private void writeEmptyLine(XSSFSheet sheet) {
        writeEmptyLine(sheet, emptyCellStyle, rowIndex, columnIndex);
    }

    /**
     * @return list of unique teacher names
     */
    private List<String> getTeacherNames() {
        return schedules.keySet().stream().sorted().collect(Collectors.toList());
    }

    private ScheduleByGroupsOrTeachers generateScheduleByTeacher(Schedule schedule) {
        return ScheduleByGroupsOrTeachers.builder()
                .oneDisciplineCipher(schedule.getDisciplineCipher())
                .facultyType(schedule.getFacultyType())
                .facultyAddress(schedule.getFacultyAddress())
                .dayOfWeek(schedule.getDayOfWeek())
                .lessonNumber(schedule.getLessonNumber())
                .typeOfWeek(schedule.getTypeOfWeek())
                .build();
    }
}


