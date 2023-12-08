package com.discipline.selection.automation.service.writer.created.impl.all;

import com.discipline.selection.automation.model.ScheduleByGroupsOrTeachers;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.Student;
import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.created.WriteScheduleForAllWorkDaysToExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_DATES_HEADER;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByGroupsToNewExcelImpl extends WriteScheduleForAllWorkDaysToExcel {

    private final Set<String> studentsGroups = new LinkedHashSet<>();

    public WriteScheduleByGroupsToNewExcelImpl(Map<String, List<Student>> studentsGroupedByGroup,
                                               Map<String, List<Schedule>> schedules, XSSFWorkbook workbook) {
        super(workbook);
        this.students = studentsGroupedByGroup;
        this.schedules = schedules;
        this.studentsGroups.addAll(getStudentsGroups());
        this.workbook = workbook;
    }

    @Override
    public void writeToExcel(String fileName) {
        System.out.println("\nЗапис розкладу груп...");
        XSSFSheet scheduleSheet = workbook.createSheet(SCHEDULE_BY_GROUPS_SHEET_NAME);
        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet);

        System.out.printf("Розклад груп було записано у новий вихiдний файл \"%s\" (Лист №1).%n", fileName);
    }

    /**
     * Void that write header for "Розклад груп".
     * That header consists of two parts: the first part - it is basic header titles and
     * the second part - it is first 2 letters of students groups.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_DATES_HEADER, columnIndex);
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
            students.get(group).forEach(student -> student.getDisciplines().stream().filter(Objects::nonNull).forEach(discipline -> {
                List<Schedule> scheduleByDisciplineForCurrentStudent =
                        schedules.get(discipline.getDisciplineCipher());
                if (scheduleByDisciplineForCurrentStudent != null) {
                    List<ScheduleByGroupsOrTeachers> schedules = mapScheduleToScheduleByGroupsOrTeachers(
                            filterSchedule(scheduleByDisciplineForCurrentStudent, day, lessonNumber, weekType));
                    schedules = schedules.stream()
                            .filter(schedule -> scheduleContainsGroup(schedule, group))
                            .collect(Collectors.toList());

                    scheduleByGroups.addAll(schedules);
                }
            }));

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


