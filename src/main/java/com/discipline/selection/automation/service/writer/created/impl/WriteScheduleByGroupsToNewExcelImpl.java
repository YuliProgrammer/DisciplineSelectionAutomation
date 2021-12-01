package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleByGroups;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.discipline.selection.automation.model.enums.WeekDay.DAYS;
import static com.discipline.selection.automation.model.enums.WeekType.WEEK_TYPES;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_BY_GROUPS_HEADER;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteScheduleByGroupsToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final Set<String> studentsGroups;
    private int rowIndex = 2;
    List<String> values = new ArrayList<>();


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

        XSSFSheet scheduleSheet = workbook.createSheet("test");

//        Set<ConsolidationOfDisciplinesSchedule> schedules = generateSchedule();
//
        writeHeader(scheduleSheet);
        writeDiscipline(scheduleSheet);
    }


    /**
     * Void that write header for "Зведення дисц шифр спец".
     * That header consists of two parts: the first part - it is basic header titles and
     * the second part - it is a first 2 letters of students classes.
     *
     * @param sheet - current new sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        int columnIndex = 0;
        writeHeader(sheet, SCHEDULE_BY_GROUPS_HEADER, columnIndex);
        writeHeader(sheet, studentsGroups, columnIndex); // write the second part of the header
    }


    /**
     * Void that write disciplines to excel.
     *
     * @param sheet - current sheet
     */
    private void writeDiscipline(XSSFSheet sheet) {
        Arrays.stream(DAYS).forEach(day -> {
            IntStream.range(1, 8).forEach(lessonNumber -> {
                Arrays.stream(WEEK_TYPES).forEach(weekType -> {
                    values.add(day.getName());
                    values.add(String.valueOf(lessonNumber));
                    values.add(weekType.getName());
                    for (String group : studentsGroups) {
                        Set<ScheduleByGroups> scheduleByGroups = new HashSet<>();
                        students.get(group).forEach(student -> {
                            List<Schedule> scheduleByDisciplineForCurrentStudent =
                                    schedules.get(student.getDiscipline().getDisciplineCipher());
                            if (scheduleByDisciplineForCurrentStudent != null) {
                                List<ScheduleByGroups> schedules =
                                        scheduleByDisciplineForCurrentStudent.stream()
                                                .filter(schedule -> schedule.getDayOfWeek().equals(day))
                                                .filter(schedule -> schedule.getLessonNumber()
                                                        .equals(lessonNumber))
                                                .filter(schedule -> schedule.getTypeOfWeek().equals(weekType))
                                                .map(this::generateScheduleByGroups)
                                                .collect(Collectors.toList());
                                scheduleByGroups.addAll(schedules);
                            }
                        });

                        Set<String> disciplineValues = new HashSet<>();
                        scheduleByGroups.stream().forEach(s -> {
                            String discipline = s.getOneDisciplineCipher() + "(" + s.getFacultyType() + ")\n";
                            disciplineValues.add(discipline);
                        });
                        values.add(String.join("", disciplineValues));
                    }

                    // write entry
                    writeEntry(sheet, values);
                    values = new ArrayList<>();
                    System.out.println(day + " " + lessonNumber + " " + weekType);
                });
            });
        });

//        for (String group : studentsGroups) {
//            Map<String, Set<ScheduleByGroups>> scheduleByGroupsAndWeekDay = new HashMap<>();
//            Set<ScheduleByGroups> scheduleByGroups = new HashSet<>();
//
//            List<Student> studentsByGroup = students.get(group);
//            studentsByGroup.forEach(student -> {
//                List<Schedule> scheduleByDisciplineForCurrentStudent =
//                        schedules.get(student.getDiscipline().getDisciplineCipher());
//                if (scheduleByDisciplineForCurrentStudent != null) {
//                    scheduleByGroups.addAll(scheduleByDisciplineForCurrentStudent.stream()
//                            .map(this::generateScheduleByGroups).collect(Collectors.toList()));
//                }
//            });
//
//            scheduleByGroups.forEach(scheduleByGroup -> {
//                String weekDay = scheduleByGroup.getDayOfWeek().getName();
//                Set<ScheduleByGroups> currentSchedule = scheduleByGroupsAndWeekDay.get(weekDay);
//                currentSchedule = currentSchedule == null ? new HashSet<>() : currentSchedule;
//                currentSchedule.add(scheduleByGroup);
//                scheduleByGroupsAndWeekDay.put(weekDay, currentSchedule);
//            });
//
//
//            Map<String, List<ScheduleByGroups>> sortedScheduleByGroupsAndWeekDay = new HashMap<>();
//            scheduleByGroupsAndWeekDay.forEach((key, value) -> {
//                List<ScheduleByGroups> sortedScheduleByGroups = value.stream()
//                        .sorted(Comparator.comparing(ScheduleByGroups::getLessonNumber))
//                        .sorted(Comparator.comparing(ScheduleByGroups::getTypeOfWeek))
//                        .collect(Collectors.toList());
//                sortedScheduleByGroupsAndWeekDay.put(key, sortedScheduleByGroups);
//            });
//
//            rowIndex++;
//            System.out.println("sortedScheduleByGroups: " + sortedScheduleByGroupsAndWeekDay);
//        }


    }

    private void writeEntry(XSSFSheet sheet, List<String> values) {
        writeEntry(sheet, setForeground(rowIndex), values, rowIndex);
        rowIndex += 1;
    }

    /**
     * @return set of unique first 2 letters of students groups
     */
    private Set<String> getStudentsGroups() {
        return students.keySet();
    }

    private ScheduleByGroups generateScheduleByGroups(Schedule schedule) {
        return ScheduleByGroups.builder()
                .oneDisciplineCipher(schedule.getDisciplineCipher())
                .dayOfWeek(schedule.getDayOfWeek())
                .lessonNumber(schedule.getLessonNumber())
                .typeOfWeek(schedule.getTypeOfWeek())
                .build();
    }
}


