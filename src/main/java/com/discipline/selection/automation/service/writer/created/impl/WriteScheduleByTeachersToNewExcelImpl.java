package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.ScheduleByTeachers;
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
public class WriteScheduleByTeachersToNewExcelImpl extends WriteScheduleByGroupsOrTeachersToExcel {

    private final Set<String> teacherNames = new LinkedHashSet<>();

    public WriteScheduleByTeachersToNewExcelImpl(Map<String, List<Schedule>> schedulesGroupedByTeacher) {
        this.schedules = schedulesGroupedByTeacher;
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
        List<String> header = new ArrayList<>();
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
            teacherDisciplines.addAll(getDisciplinesForAllTeachersAndOneLesson(scheduleByTeacher));
        }

        return teacherDisciplines.stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * @param scheduleByTeachers - list of schedules for current teacher
     * @return list of values for current teacher
     */
    private List<String> getDisciplinesForAllTeachersAndOneLesson(
            Set<ScheduleByGroupsOrTeachers> scheduleByTeachers) {
        Set<ScheduleByTeachers> disciplineValues = new HashSet<>();
        scheduleByTeachers.forEach(schedule -> {
            disciplineValues.add(ScheduleByTeachers.builder()
                    .disciplineCipher(schedule.getOneDisciplineCipher())
                    .facultyAddress(schedule.getFacultyAddress())
                    .lessonType(schedule.getLessonType())
                    .fileName(schedule.getFileName())
                    .build());
        });

        List<String> teacherDisciplines = new ArrayList<>();
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByTeachers::getDisciplineCipher)
                .collect(Collectors.joining()));
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByTeachers::getFacultyAddress)
                .collect(Collectors.joining()));
        teacherDisciplines.add(disciplineValues.stream()
                .map(scheduleByTeacher -> scheduleByTeacher.getLessonType().getName())
                .collect(Collectors.joining()));
        teacherDisciplines.add(disciplineValues.stream()
                .map(ScheduleByTeachers::getFileName)
                .collect(Collectors.joining()));
        return teacherDisciplines;
    }

    /**
     * @return list of unique teacher names
     */
    private List<String> getTeacherNames() {
        return schedules.keySet().stream().sorted().collect(Collectors.toList());
    }

}


