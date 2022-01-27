package com.discipline.selection.automation.service.writer.common.impl;

import com.discipline.selection.automation.mapper.StudentMapper;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.WriteToExcel;
import com.discipline.selection.automation.service.writer.common.Writer;
import com.discipline.selection.automation.service.writer.created.impl.WriteConsolidationOfDisciplinesScheduleToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.WriteConsolidationOfDisciplinesToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.WriteScheduleByGroupsToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.WriteScheduleByTeachersToNewExcelImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.model.enums.LessonType.LABORATORY;
import static com.discipline.selection.automation.model.enums.LessonType.PRACTICE;
import static com.discipline.selection.automation.util.Constants.OUTPUT_FILE_NAME;
import static com.discipline.selection.automation.util.Constants.OUTPUT_FILE_NAME_SCHEDULE;

/**
 * Class that calls of WriteConsolidationOfDisciplinesToNewExcelImpl and WriteConsolidationOfDisciplinesScheduleToNewExcelImpl
 * in order to optimize saving the result in one excel file
 *
 * @author Yuliia_Dolnikova
 */
public class WriteConsolidationOfDisciplines implements Writer {

    private final Map<String, List<Student>> studentsGroupedByDiscipline;
    private final Map<String, List<Student>> studentsGroupedByGroup;
    private final Map<String, Discipline> disciplines;
    private final Map<String, List<Schedule>> schedule;
    private final Map<String, List<Schedule>> schedulesGroupedByTeacher;
    private final Set<String> disciplinesWithoutSchedule = new LinkedHashSet<>();

    public WriteConsolidationOfDisciplines(Map<String, List<Student>> studentsGroupedByGroup,
                                           Map<String, List<Student>> studentsGroupedByDiscipline,
                                           Map<String, Discipline> disciplines,
                                           Map<String, List<Schedule>> schedule,
                                           Map<String, List<Schedule>> schedulesGroupedByTeacher) {
        this.studentsGroupedByGroup = studentsGroupedByGroup;
        this.studentsGroupedByDiscipline =
                StudentMapper.getStudentsGroupedByDisciplineCipherForDifferentFacilities(studentsGroupedByDiscipline);
        this.disciplines = disciplines.entrySet().stream()
                .filter(entry -> this.studentsGroupedByDiscipline.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.schedule = schedule;
        this.schedulesGroupedByTeacher = schedulesGroupedByTeacher;
        addMaxStudentCountForPracticeAndLaboratory(schedule, disciplines);
    }

    /**
     * Void add max students count for practices and laboratories where max number of students in sub group is null.
     *
     * @param schedule    - schedule for all students and disciplines
     * @param disciplines - disciplines chosen by students from different faculties
     */
    private void addMaxStudentCountForPracticeAndLaboratory(Map<String, List<Schedule>> schedule,
                                                            Map<String, Discipline> disciplines) {
        disciplines.forEach((key, value) -> {
            List<Schedule> scheduleByDiscipline = schedule.get(key);
            if (scheduleByDiscipline == null) {
                scheduleByDiscipline = new ArrayList<>();
                disciplinesWithoutSchedule.add(key);
            }
            scheduleByDiscipline.stream()
                    .filter(s -> s.getLessonType().equals(PRACTICE) || s.getLessonType().equals(LABORATORY))
                    .filter(s -> s.getMaxNumberOfStudentsInSubGroup() == null)
                    .forEach(s -> s.setMaxNumberOfStudentsInSubGroup(value.getNumberOfStudentsInSubGroup()));
        });
    }

    public void writeToExcel() {
        writeConsolidation();
        writeSchedule();
    }

    private void writeConsolidation() {
        System.out.println("\nЗапис зведення дисциплiн...");
        WriteToExcel writeConsolidationOfDisciplines =
                new WriteConsolidationOfDisciplinesToNewExcelImpl(studentsGroupedByDiscipline, disciplines, schedule);
        WriteToExcel writeConsolidationOfDisciplinesScheduleToNewExcel =
                new WriteConsolidationOfDisciplinesScheduleToNewExcelImpl(studentsGroupedByDiscipline, disciplines,
                        schedule,
                        disciplinesWithoutSchedule);

        String fileName = getFileNameForConsolidation();
        File file = new File(fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            writeConsolidationOfDisciplines.writeToExcel(workbook);
            writeConsolidationOfDisciplinesScheduleToNewExcel.writeToExcel(workbook);

            writeToWorkbook(file, workbook);
            System.out.printf("Зведення дисциплiн було записано у новий вихiдний файл \"%s\" (Лист №1).%n", fileName);
            System.out.printf("Розклад студентiв було записано у новий вихiдний файл \"%s\" (Лист №2).%n", fileName);
            System.out.printf("Дублiкати розкладу студентiв було записано у новий вихiдний файл \"%s\" (Лист №3).%n",
                    fileName);
            System.out.printf("Проблеми з переїздом було записано у новий вихiдний файл \"%s\" (Лист №4).%n", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeSchedule() {
        WriteToExcel writeScheduleByGroups =
                new WriteScheduleByGroupsToNewExcelImpl(studentsGroupedByGroup, schedule);
        WriteToExcel writeScheduleByTeachers =
                new WriteScheduleByTeachersToNewExcelImpl(schedulesGroupedByTeacher);
        String fileName = getFileNameForSchedule();
        File file = new File(fileName);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            System.out.println("\nЗапис розкладу груп...");
            writeScheduleByGroups.writeToExcel(workbook);
            System.out.printf("Розклад груп було записано у новий вихiдний файл \"%s\" (Лист №1).%n", fileName);

            System.out.println("\nЗапис розкладу НПП та прогблем із цим розкладом...");
            writeScheduleByTeachers.writeToExcel(workbook);
            System.out.printf("Розклад НПП було записано у новий вихiдний файл \"%s\" (Лист №2).%n", fileName);
            System.out.printf("Проблеми із розклад НПП було записано у новий вихiдний файл \"%s\" (Лист №3).%n", fileName);

            writeToWorkbook(file, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Void generate name of new output file.
     *
     * @return file name
     */
    private String getFileNameForConsolidation() {
        return getFileName() + OUTPUT_FILE_NAME;
    }

    /**
     * Void generate name of new output file.
     *
     * @return file name
     */
    private String getFileNameForSchedule() {
        return getFileName() + OUTPUT_FILE_NAME_SCHEDULE;
    }

    private String getFileName() {
        String separator = File.separator;
        String inputFileName = FILE_NAME;
        int indexOfLastSlash = inputFileName.contains(separator) ? inputFileName.lastIndexOf(separator) + 1 : 0;

        String outputPath = inputFileName.substring(0, indexOfLastSlash);

        String outputYear = getYear();
        return outputPath + outputYear;
    }

    /**
     * If the current date after January 1 and before September 10 - @return current year.
     * In other cases - @return next year.
     */
    private String getYear() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();

        LocalDate minCurrentDate = LocalDate.of(currentYear, 1, 1);  // January 1
        LocalDate maxCurrentDate = LocalDate.of(currentYear, 9, 10); // September 10

        return currentDate.isAfter(minCurrentDate) && currentDate.isBefore(maxCurrentDate) ?
                String.valueOf(currentYear) : String.valueOf(currentYear + 1);
    }

}
