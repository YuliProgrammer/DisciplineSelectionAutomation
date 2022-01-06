package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.model.enums.FacultyType;
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
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByGroupsToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final List<String> studentsGroups;
    private int rowIndex = 2;
    private int columnIndex = 0;
    private List<String> values = new ArrayList<>();

    public WriteScheduleByGroupsToNewExcelImpl(Map<String, List<Student>> studentsGroupedByGroup,
                                               Map<String, Discipline> disciplines,
                                               Map<String, List<Schedule>> schedules) {
        this.students = studentsGroupedByGroup;
        this.disciplines = disciplines;
        this.schedules = schedules;
        this.studentsGroups = getStudentsGroups();
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);
        XSSFSheet scheduleSheet = workbook.createSheet(SCHEDULE_BY_GROUPS_SHEET_NAME);
        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet);
    }


    /**
     * Void that write header for "Розклад груп".
     * That header consists of two parts: the first part - it is basic header titles and
     * the second part - it is a first 2 letters of students classes.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER, columnIndex);
        columnIndex = writeHeader(sheet, new LinkedHashSet<>(studentsGroups),
                columnIndex); // write the second part of the header
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
                    values.addAll(getValuesForAllGroups(day, lessonNumber, weekType));
                    writeEntry(sheet, values);
                });
            });
            writeEmptyLine(sheet);
        });
    }

    /**
     * Method get schedules  for all groups that corresponds to current parameters
     *
     * @param day          - the day of week on which the discipline is to be held
     * @param lessonNumber - the lesson number on which the discipline is to be held
     * @param weekType     - the week type on which the discipline is to be held
     * @return list of discipline ciphers. Each element - it is an enumeration of disciplines for one group
     */
    private List<String> getValuesForAllGroups(WeekDay day, int lessonNumber, WeekType weekType) {
        List<String> groupDisciplines = new ArrayList<>();
        for (String group : studentsGroups) {
            Set<ScheduleByGroupsOrTeachers> scheduleByGroups = new HashSet<>();
            students.get(group).forEach(student -> {
                List<Schedule> scheduleByDisciplineForCurrentStudent =
                        schedules.get(student.getDiscipline().getDisciplineCipher());
                if (scheduleByDisciplineForCurrentStudent != null) {
                    List<ScheduleByGroupsOrTeachers> schedules = filterScheduleForStudent(scheduleByDisciplineForCurrentStudent,
                            group, day, lessonNumber, weekType);
                    scheduleByGroups.addAll(schedules);
                }
            });

            Set<String> disciplineValues = getDisciplinesForAllGroupAndOneLesson(scheduleByGroups);
            groupDisciplines.add(String.join("", disciplineValues));
        }

        return groupDisciplines.stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * @param scheduleByDisciplineForCurrentStudent - list of schedules for current student
     * @param group                                 - group of current student
     * @param day                                   - the day of week on which the discipline is to be held
     * @param lessonNumber                          - the lesson number on which the discipline is to be held
     * @param weekType                              - the week type on which the discipline is to be held
     * @return list of schedules for current student that that corresponds to current parameters
     */
    private List<ScheduleByGroupsOrTeachers> filterScheduleForStudent(List<Schedule> scheduleByDisciplineForCurrentStudent,
                                                                      String group, WeekDay day, int lessonNumber,
                                                                      WeekType weekType) {
        return scheduleByDisciplineForCurrentStudent.stream()
                .filter(schedule -> scheduleContainsGroup(schedule, group))
                .filter(schedule -> schedule.getDayOfWeek().equals(day))
                .filter(schedule -> schedule.getLessonNumber()
                        .equals(lessonNumber))
                .filter(schedule -> schedule.getTypeOfWeek().equals(weekType) ||
                        (!weekType.equals(EVERY_WEEK) && schedule.getTypeOfWeek().equals(EVERY_WEEK)))
                .map(this::generateScheduleByGroups)
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
     * @param scheduleByGroups - list of schedules for current group
     * @return set of disciplines ciphers for current group
     */
    private Set<String> getDisciplinesForAllGroupAndOneLesson(Set<ScheduleByGroupsOrTeachers> scheduleByGroups) {
        Set<String> disciplineValues = new HashSet<>();
        scheduleByGroups.forEach(s -> {
            FacultyType facultyType = s.getFacultyType();
            String faculty = facultyType != null ? "(" + facultyType.getType() + ")" : "";
            String discipline = s.getOneDisciplineCipher() + faculty + "\n";
            disciplineValues.add(discipline);
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
     * @return list of unique first 2 letters of students groups
     */
    private List<String> getStudentsGroups() {
        return students.keySet().stream().sorted().collect(Collectors.toList());
    }

    private ScheduleByGroupsOrTeachers generateScheduleByGroups(Schedule schedule) {
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


