package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.ScheduleByTeachers;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.WriteScheduleByGroupsOrTeachersToExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_HEADER;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_TEACHER_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByTeachersGroupsOrTeachersToNewExcelImpl extends WriteScheduleByGroupsOrTeachersToExcel {

    private final Set<String> teacherNames = new LinkedHashSet<>();

    public WriteScheduleByTeachersGroupsOrTeachersToNewExcelImpl(Map<String, List<Schedule>> schedulesGroupedByTeacher,
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
     * the second part - it is teacher names.
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
     * Method get schedules  for all teachers that corresponds to current parameters
     *
     * @param day          - the day of week on which the discipline is to be held
     * @param lessonNumber - the lesson number on which the discipline is to be held
     * @param weekType     - the week type on which the discipline is to be held
     * @return list of discipline ciphers. Each element - it is an enumeration of disciplines for one teacher
     */
    protected List<String> getValuesForAll(WeekDay day, int lessonNumber, WeekType weekType) {
        List<String> teacherDisciplines = new ArrayList<>();
        for (String teacherName : teacherNames) {
            List<Schedule> scheduleByCurrentTeacher = schedules.get(teacherName);
            Set<ScheduleByGroupsOrTeachers> scheduleByTeacher =
                    new HashSet<>(filterSchedule(scheduleByCurrentTeacher, day, lessonNumber, weekType));
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

    /**
     * @return list of unique teacher names
     */
    private List<String> getTeacherNames() {
        return schedules.keySet().stream().sorted().collect(Collectors.toList());
    }

}


