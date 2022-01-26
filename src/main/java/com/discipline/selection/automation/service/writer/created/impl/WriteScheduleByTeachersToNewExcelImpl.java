package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.WriteScheduleByGroupsOrTeachersToExcel;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_PROBLEMS_BY_TEACHER_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.SEMICOLON;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByTeachersToNewExcelImpl extends WriteScheduleByGroupsOrTeachersToExcel {

    private Set<String> teacherNames = new LinkedHashSet<>();
    private final Map<String, List<Schedule>> teachersDuplicates = new HashMap<>();

    public WriteScheduleByTeachersToNewExcelImpl(Map<String, List<Schedule>> schedulesGroupedByTeacher) {
        this.schedules = schedulesGroupedByTeacher;
        this.teacherNames.addAll(getTeacherNames());
    }

    /**
     * The void writes 2 sheets into workbook "Розклад_груп_та_НПП":
     * 1. teachers' schedule
     * 2. problems with this schedule
     *
     * @param workbook - current workbook
     */
    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);
        writeTeachersSchedule(workbook, SCHEDULE_BY_TEACHER_SHEET_NAME, duplicatedCellStyle);
        if (!teachersDuplicates.isEmpty()) {
            prepareDataForWritingDuplicates();
            writeTeachersSchedule(workbook, SCHEDULE_PROBLEMS_BY_TEACHER_SHEET_NAME);
        }
    }

    /**
     * The void writes teachers' schedule into Excel sheet
     *
     * @param workbook  - current workbook
     * @param sheetName - name of current Excel sheet
     */
    private void writeTeachersSchedule(XSSFWorkbook workbook, String sheetName, CellStyle... duplicatedCellStyle) {
        XSSFSheet scheduleSheet = workbook.createSheet(sheetName);
        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet, duplicatedCellStyle);
    }

    /**
     * The void prepare date for writing problems with teachers' schedule into Excel sheet "Розклад НПП (Проблеми)"
     **/
    private void prepareDataForWritingDuplicates() {
        this.teacherNames = new HashSet<>(teachersDuplicates.keySet());
        this.schedules = teachersDuplicates;
        this.columnIndex = 0;
        this.rowIndex = 2;
    }

    /**
     * Void that write header for sheets: "Розклад НПП" and "Розклад НПП (Проблеми)"
     * That header consists of two parts: the first part - it is basic header titles (day, lesson number and week type)
     * and the second part - it is teacher names with discipline values.
     *
     * @param sheet - current new Excel sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER, columnIndex);
        List<String> header = new ArrayList<>();
        this.teacherNames.forEach(teacherName -> {
            header.add(teacherName);
            header.addAll(SCHEDULE_BY_TEACHER_HEADER);
        });
        columnIndex = writeHeader(sheet, header, columnIndex); // write the second part of the header
    }

    /**
     * Method get schedules  for all teachers that corresponds to current parameters
     *
     * @param day          - the day of week on which the discipline is to be held
     * @param lessonNumber - the lesson number on which the discipline is to be held
     * @param weekType     - the week type on which the discipline is to be held
     * @return list of discipline values (discipline cipher, faculty, lesson type and file name)
     */
    protected List<String> getValuesForAll(WeekDay day, int lessonNumber, WeekType weekType) {
        List<String> teacherDisciplines = new ArrayList<>();
        for (String teacherName : teacherNames) {
            List<Schedule> scheduleByCurrentTeacher = schedules.get(teacherName);
            Set<ScheduleByGroupsOrTeachers> scheduleByTeacher =
                    new HashSet<>(filterSchedule(scheduleByCurrentTeacher, day, lessonNumber, weekType));
            List<String> disciplinesForTeachersAndOneLesson = getDisciplinesForTeacherAndOneLesson(scheduleByTeacher);
            if (disciplinesForTeachersAndOneLesson.get(0).contains(SEMICOLON)) {
                getTeachersDuplicates(scheduleByCurrentTeacher, teacherName);
            }
            teacherDisciplines.addAll(disciplinesForTeachersAndOneLesson);
        }
        return teacherDisciplines.stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * Method get list of values for current teacher
     *
     * @param disciplineValues - list of schedules for current teacher
     * @return list of values for current teacher (discipline cipher, faculty, lesson type and file name).
     * When a teacher has some duplicates (different disciplines at one lesson)
     * then each result element contains semicolon.
     */
    private List<String> getDisciplinesForTeacherAndOneLesson(Set<ScheduleByGroupsOrTeachers> disciplineValues) {
        List<String> teacherDisciplines = new ArrayList<>();
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByGroupsOrTeachers::getOneDisciplineCipher)
                .collect(Collectors.joining(SEMICOLON)));
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByGroupsOrTeachers::getFacultyAddress)
                .collect(Collectors.joining(SEMICOLON)));
        teacherDisciplines.add(disciplineValues.stream()
                .map(scheduleByTeacher -> scheduleByTeacher.getLessonType().getName())
                .collect(Collectors.joining(SEMICOLON)));
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByGroupsOrTeachers::getFileName)
                .collect(Collectors.joining(SEMICOLON)));
        return teacherDisciplines;
    }

    /**
     * Void add duplicated lessons into additional map - teachersDuplicates
     *
     * @param scheduleByCurrentTeacher -  list of schedules for current teacher
     * @param teacherName              - name of current teacher
     */
    private void getTeachersDuplicates(List<Schedule> scheduleByCurrentTeacher, String teacherName) {
        scheduleByCurrentTeacher.forEach(scheduleByTeacher -> scheduleByTeacher.setTeacherName(teacherName));
        Set<Schedule> scheduleByTeacher = this.teachersDuplicates.containsKey(teacherName) ?
                new HashSet<>(this.teachersDuplicates.get(teacherName)) : new HashSet<>();
        scheduleByTeacher.addAll(scheduleByCurrentTeacher);
        teachersDuplicates.put(teacherName, new ArrayList<>(scheduleByTeacher));
    }

    /**
     * Method return list of all teachers' names
     *
     * @return list of unique teachers' names
     */
    private List<String> getTeacherNames() {
        return schedules.keySet().stream().sorted().collect(Collectors.toList());
    }

}


