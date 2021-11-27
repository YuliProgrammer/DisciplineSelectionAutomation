package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.ConsolidationOfDisciplinesSchedule;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.MainApplication.FILE_NAMES;
import static com.discipline.selection.automation.model.enums.LessonType.LABORATORY;
import static com.discipline.selection.automation.model.enums.LessonType.LECTURE;
import static com.discipline.selection.automation.model.enums.LessonType.PRACTICE;
import static com.discipline.selection.automation.util.Constants.COMA;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_DUPLICATED_SCHEDULE_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteConsolidationOfDisciplinesScheduleToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final Set<String> disciplinesWithoutSchedule;

    public WriteConsolidationOfDisciplinesScheduleToNewExcelImpl(Map<String, List<Student>> students,
                                                                 Map<String, Discipline> disciplines,
                                                                 Map<String, List<Schedule>> schedules,
                                                                 Set<String> disciplinesWithoutSchedule) {
        this.students = students;
        this.disciplines = disciplines;
        this.schedules = schedules;
        this.disciplinesWithoutSchedule = disciplinesWithoutSchedule;
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);

        XSSFSheet scheduleSheet = workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_SHEET_NAME);
        XSSFSheet duplicatedScheduleSheet =
                workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_DUPLICATED_SCHEDULE_SHEET_NAME);

        Set<ConsolidationOfDisciplinesSchedule> schedules = generateSchedule();
        Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule = isDuplicate(schedules);
        duplicatedSchedule.forEach(consolidation -> consolidation.setDuplicate(false)); // for good view in Excel sheet

        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet, schedules);
        writeDuplicatesCount(scheduleSheet, duplicatedSchedule.size());

        writeHeader(duplicatedScheduleSheet);
        writeSchedule(duplicatedScheduleSheet, duplicatedSchedule);

        if (!disciplinesWithoutSchedule.isEmpty()) {
            System.out.println(String.format("\nВ файлi \"%s\" немає розкладу для дисциплiн [%s].\n",
                    FILE_NAMES.get(1), String.join(COMA, disciplinesWithoutSchedule)));
        }
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
        writeHeader(sheet, CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER, columnIndex);
    }

    /**
     * Void that generate schedule for all students.
     *
     * @return schedule for all students
     */
    private Set<ConsolidationOfDisciplinesSchedule> generateSchedule() {
        Set<ConsolidationOfDisciplinesSchedule> schedules = new LinkedHashSet<>();

        for (Map.Entry<String, List<Student>> studentsByDiscipline : students.entrySet()) {
            String disciplineCipher = studentsByDiscipline.getKey();
            if (this.schedules.containsKey(disciplineCipher)) {
                for (Student student : studentsByDiscipline.getValue()) {
                    if (disciplineCipher.equals("1у-1-5")) {
                        schedules.addAll(createConsolidationOfDisciplinesSchedule(student, disciplineCipher));
                    } else {
                        schedules.addAll(createConsolidationOfDisciplinesSchedule(student, disciplineCipher));

                    }
                }
            }
        }

        return schedules;
    }

    /**
     * Void that write schedule to excel.
     *
     * @param sheet     - current sheet
     * @param schedules - list of schedules
     */
    private void writeSchedule(XSSFSheet sheet, Set<ConsolidationOfDisciplinesSchedule> schedules) {
        int rowIndex = 2;
        for (ConsolidationOfDisciplinesSchedule schedule : schedules) {
            writeEntry(sheet, setScheduleForeground(rowIndex, schedule.isDuplicate()),
                    schedule.getValuesForConsolidationOfDisciplineSchedule(), rowIndex);
            rowIndex++;
        }
    }

    /**
     * Void writes count of duplicated disciplines to the new cell.
     *
     * @param sheet           - current sheet
     * @param duplicatesCount - count of duplicated schedules
     */
    private void writeDuplicatesCount(XSSFSheet sheet, int duplicatesCount) {
        Row firstRow = sheet.getRow(0);
        Cell cell = firstRow.createCell(CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER.size() + 1);
        cell.setCellStyle(CellStyleCreator.createMainHeaderCellStyleCharacteristics(sheet.getWorkbook()));
        cell.setCellValue("Виявлена кiлькiсть дублiкатiв: " + duplicatesCount);
    }

    /**
     * Void creates schedule for current student and discipline.
     *
     * @param student          - student for making schedule
     * @param disciplineCipher - cipher of current discipline
     * @return list of ConsolidationOfDisciplinesSchedule for this student and discipline
     */
    private Set<ConsolidationOfDisciplinesSchedule> createConsolidationOfDisciplinesSchedule(Student student,
                                                                                             String disciplineCipher) {
        Set<ConsolidationOfDisciplinesSchedule> schedules = new LinkedHashSet<>();
        List<Schedule> scheduleForCurrentDisciplineCipher = this.schedules.get(disciplineCipher);

        if (scheduleForCurrentDisciplineCipher == null) {
            scheduleForCurrentDisciplineCipher = new ArrayList<>();
            disciplinesWithoutSchedule.add(disciplineCipher);
        }

        int indexOfLastHyphen = student.getGroup().lastIndexOf("-");
        String studentGroupCode = student.getGroup().substring(0, indexOfLastHyphen);

        List<Schedule> scheduleForDisciplineAndStudentGroup =
                scheduleForCurrentDisciplineCipher.stream()
                        .filter(schedule -> schedule.getGroupCodes().contains(studentGroupCode))
                        // If the lesson type is lecture than get all lesson with this type.
                        // If the lesson type is practice or laboratory than get only those lessons where
                        // current student count is less than maximum students count.
                        .filter(schedule -> schedule.getLessonType().equals(LECTURE) ||
                                schedule.getNumberOfStudentsInSubGroup() <
                                        schedule.getMaxNumberOfStudentsInSubGroup())
                        // .filter(FindDistinct.distinctByKey(Schedule::getLessonType))
                        .collect(Collectors.toList());

        for (Schedule schedule : scheduleForDisciplineAndStudentGroup) {
            if (schedule.getLessonType().equals(LABORATORY) || schedule.getLessonType().equals(PRACTICE)) {
                schedule.setNumberOfStudentsInSubGroup(schedule.getNumberOfStudentsInSubGroup() + 1);
            }

            ConsolidationOfDisciplinesSchedule consolidationOfDisciplinesSchedule =
                    createConsolidationOfDisciplinesSchedule(disciplineCipher, student);
            consolidationOfDisciplinesSchedule.setSchedule(schedule);
            schedules.add(consolidationOfDisciplinesSchedule);
        }

        return schedules;
    }

    /**
     * Void create ConsolidationOfDisciplinesSchedule from current student and discipline.
     *
     * @param student          - student for making schedule
     * @param disciplineCipher - cipher of current discipline
     * @return ConsolidationOfDisciplinesSchedule for this student and discipline
     */
    private ConsolidationOfDisciplinesSchedule createConsolidationOfDisciplinesSchedule(String disciplineCipher,
                                                                                        Student student) {
        ConsolidationOfDisciplinesSchedule consolidationOfDisciplinesSchedule =
                new ConsolidationOfDisciplinesSchedule();
        consolidationOfDisciplinesSchedule.setFacilityFirstLetter(student.getFacilityCipher().substring(0, 1));
        consolidationOfDisciplinesSchedule.setDisciplineCipher(disciplineCipher);
        consolidationOfDisciplinesSchedule.setDisciplineName(student.getDiscipline().getDisciplineName());
        consolidationOfDisciplinesSchedule.setStudentName(student.getName());
        consolidationOfDisciplinesSchedule.setGroup(student.getGroup());

        return consolidationOfDisciplinesSchedule;
    }

    /**
     * Void checks all schedule's rows and mark duplicated rows as `Duplicate`.
     * Two schedule's rows be considered duplicates when:
     * 1. They have the same types of week or one of types - it is a EVERY_WEEK.
     * 2. They have the same days of week.
     * 3. They have different discipline cipher.
     *
     * @param schedules -  full schedule for all students and disciplines that has chosen by students from different faculties
     * @return schedule that contain only duplicated items
     */
    private Set<ConsolidationOfDisciplinesSchedule> isDuplicate(Set<ConsolidationOfDisciplinesSchedule> schedules) {
        Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule = new LinkedHashSet<>();

        schedules.forEach(disciplinesSchedule -> {
            Schedule currentSchedule = disciplinesSchedule.getSchedule();
            schedules.stream()
                    .filter(consolidation -> consolidation.getStudentName()
                            .equals(disciplinesSchedule.getStudentName()))
                    .filter(consolidation ->
                            consolidation.getSchedule().getTypeOfWeek().equals(currentSchedule.getTypeOfWeek()) ||
                                    (consolidation.getSchedule().getTypeOfWeek() != WeekType.EVERY_WEEK &&
                                            currentSchedule.getTypeOfWeek() == WeekType.EVERY_WEEK) ||
                                    (consolidation.getSchedule().getTypeOfWeek() == WeekType.EVERY_WEEK &&
                                            currentSchedule.getTypeOfWeek() != WeekType.EVERY_WEEK))
                    .filter(consolidation -> consolidation.getSchedule().getDayOfWeek()
                            .equals(currentSchedule.getDayOfWeek()))
                    .filter(consolidation -> consolidation.getSchedule().getLessonNumber()
                            .equals(currentSchedule.getLessonNumber()))
                    .filter(consolidation -> !consolidation.getSchedule().getDisciplineCipher()
                            .equals(disciplinesSchedule.getDisciplineCipher()))
                    .forEach(consolidation -> {
                        consolidation.setDuplicate(true);
                        duplicatedSchedule.add(consolidation);
                    });
        });

        return duplicatedSchedule;
    }

    /**
     * @param rowIndex    - index of current row for identification this rows like odd or even.
     * @param isDuplicate - boolean value that shows if the current row is duplicate. If true - the row`s color is red.
     * @return cell style.
     */
    private XSSFCellStyle setScheduleForeground(int rowIndex, boolean isDuplicate) {
        if (isDuplicate) {
            return duplicatedCellStyle;
        }
        return setForeground(rowIndex);
    }

}


