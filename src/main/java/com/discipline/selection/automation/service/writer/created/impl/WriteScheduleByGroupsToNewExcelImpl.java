package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.model.enums.FacultyType;
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
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByGroupsToNewExcelImpl extends WriteScheduleByGroupsOrTeachersToExcel {

    private final Set<String> studentsGroups = new LinkedHashSet<>();

    public WriteScheduleByGroupsToNewExcelImpl(Map<String, List<Student>> studentsGroupedByGroup,
                                               Map<String, List<Schedule>> schedules) {
        this.students = studentsGroupedByGroup;
        this.schedules = schedules;
        this.studentsGroups.addAll(getStudentsGroups());
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
     * the second part - it is first 2 letters of students groups.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_BY_GROUPS_AND_TEACHERS_HEADER, columnIndex);
        columnIndex = writeHeader(sheet, studentsGroups, columnIndex); // write the second part of the header
    }

    /**
     * Method get schedules  for all groups that corresponds to current parameters
     *
     * @param day          - the day of week on which the discipline is to be held
     * @param lessonNumber - the lesson number on which the discipline is to be held
     * @param weekType     - the week type on which the discipline is to be held
     * @return list of discipline ciphers. Each element - it is an enumeration of disciplines for one group
     */
    protected List<String> getValuesForAll(WeekDay day, int lessonNumber, WeekType weekType) {
        List<String> groupDisciplines = new ArrayList<>();
        for (String group : studentsGroups) {
            Set<ScheduleByGroupsOrTeachers> scheduleByGroups = new HashSet<>();
            students.get(group).forEach(student -> {
                List<Schedule> scheduleByDisciplineForCurrentStudent =
                        schedules.get(student.getDiscipline().getDisciplineCipher());
                if (scheduleByDisciplineForCurrentStudent != null) {
                    List<ScheduleByGroupsOrTeachers> schedules = filterSchedule(scheduleByDisciplineForCurrentStudent,
                            day, lessonNumber, weekType);
                    schedules = schedules.stream()
                            .filter(schedule -> scheduleContainsGroup(schedule, group))
                            .collect(Collectors.toList());

                    scheduleByGroups.addAll(schedules);
                }
            });

            Set<String> disciplineValues = getDisciplinesForAllGroupAndOneLesson(scheduleByGroups);
            groupDisciplines.add(String.join("", disciplineValues));
        }

        return groupDisciplines.stream().map(String::trim).collect(Collectors.toList());
    }

    /**
     * @param schedule - current schedule
     * @param group    - current group
     * @return true when current group starts with one of group code from the current schedule
     */
    private boolean scheduleContainsGroup(ScheduleByGroupsOrTeachers schedule, String group) {
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

    /**
     * @return list of unique first 2 letters of students groups
     */
    private List<String> getStudentsGroups() {
        return students.keySet().stream().sorted().collect(Collectors.toList());
    }

}


