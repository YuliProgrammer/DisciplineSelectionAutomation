package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.mapper.StringMapper;
import com.discipline.selection.automation.model.ConsolidationOfDisciplinesSchedule;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.model.enums.LessonType;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.created.WriteDisciplinesToNewExcel;
import com.discipline.selection.automation.service.writer.created.impl.all.WritePossibleTimesForDuplicatesLectures;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.model.enums.LessonType.LABORATORY;
import static com.discipline.selection.automation.model.enums.LessonType.LECTURE;
import static com.discipline.selection.automation.model.enums.LessonType.PRACTICE;
import static com.discipline.selection.automation.util.Constants.COMA;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_DUPLICATED_SCHEDULE_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_FAR_SCHEDULE_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER;
import static com.discipline.selection.automation.util.Constants.CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_SHEET_NAME;

/**
 * Class that creates the schedule of disciplines for students and
 * writes this schedule to a new Excel file.
 * Also searches for duplicates and far faculties and write them into new Excel files.
 *
 * @author Yuliia_Dolnikova
 */
public class WriteConsolidationOfDisciplinesScheduleToNewExcelImpl extends WriteDisciplinesToNewExcel {

    private final Set<String> disciplinesWithoutSchedule;
    private Set<ConsolidationOfDisciplinesSchedule> consolidationSchedules;

    public WriteConsolidationOfDisciplinesScheduleToNewExcelImpl(Map<String, List<Student>> students,
                                                                 Map<String, Discipline> disciplines,
                                                                 Map<String, List<Schedule>> schedules,
                                                                 Set<String> disciplinesWithoutSchedule,
                                                                 XSSFWorkbook workbook) {
        super(workbook);
        this.students = students;
        this.disciplines = disciplines;
        this.schedules = schedules;
        this.disciplinesWithoutSchedule = disciplinesWithoutSchedule;
        this.workbook = workbook;
    }

    @Override
    public void writeToExcel(String fileName) {
        System.out.println("\nЗапис консолідації дисциплін...");

        XSSFSheet scheduleSheet = workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_SHEET_NAME);
        XSSFSheet duplicatedScheduleSheet =
                workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_DUPLICATED_SCHEDULE_SHEET_NAME);
        XSSFSheet farScheduleSheet =
                workbook.createSheet(CONSOLIDATION_OF_DISCIPLINES_FAR_SCHEDULE_SHEET_NAME);

        consolidationSchedules = generateSchedule();
        Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule = getDuplicates(consolidationSchedules);
        Set<ConsolidationOfDisciplinesSchedule> farFacultiesSchedule = getAddressesFar(consolidationSchedules);

        writeHeader(scheduleSheet);
        writeSchedule(scheduleSheet, consolidationSchedules);
        writeDuplicatesCount(scheduleSheet, duplicatedSchedule.size());
        writeFarLessonsCount(scheduleSheet, farFacultiesSchedule.size());
        System.out.printf("Розклад студентiв було записано у новий вихiдний файл \"%s\" (Лист №2).%n", fileName);

        // for good view in other Excel sheets
        duplicatedSchedule.forEach(consolidation -> consolidation.setDuplicate(false));
        farFacultiesSchedule.forEach(consolidation -> consolidation.setFacultiesFar(false));

        // write duplicated schedule to separate sheet
        writeHeader(duplicatedScheduleSheet);
        writeSchedule(duplicatedScheduleSheet, duplicatedSchedule);
        System.out.printf("Дублiкати розкладу студентiв було записано у новий вихiдний файл \"%s\" (Лист №3).%n", fileName);

        // write far schedule to separate sheet
        writeHeader(farScheduleSheet);
        writeSchedule(farScheduleSheet, farFacultiesSchedule);
        System.out.printf("Проблеми з переїздом було записано у новий вихiдний файл \"%s\" (Лист №4).%n", fileName);

        if (!disciplinesWithoutSchedule.isEmpty()) {
            System.out.printf("\nВ файлi \"%s\" немає розкладу для дисциплiн [%s].\n%n", FILE_NAME,
                    String.join(COMA, disciplinesWithoutSchedule));
        }
        if (!duplicatedSchedule.isEmpty()) {
            WriteDisciplinesToNewExcel writeDisciplinesToNewExcel = new WritePossibleTimesForDuplicatesLectures(
                    consolidationSchedules, duplicatedSchedule, workbook);
            writeDisciplinesToNewExcel.writeToExcel(fileName);
        }
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
                    schedules.addAll(createConsolidationOfDisciplinesSchedule(student, disciplineCipher));
                }
            }
        }

        return schedules;
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
     * Void that write schedule to excel.
     *
     * @param sheet     - current sheet
     * @param schedules - list of schedules
     */
    private void writeSchedule(XSSFSheet sheet, Set<ConsolidationOfDisciplinesSchedule> schedules) {
        int rowIndex = 2;
        List<ConsolidationOfDisciplinesSchedule> sortedSchedules = schedules.stream()
                .sorted(Comparator.comparing(ConsolidationOfDisciplinesSchedule::getStudentName))
                .collect(Collectors.toList());
        for (ConsolidationOfDisciplinesSchedule schedule : sortedSchedules) {
            writeEntry(sheet, setScheduleForeground(rowIndex, schedule.isDuplicate(), schedule.isFacultiesFar()),
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
        cell.setCellValue("Виявлена к-ть дублiкатiв: " + duplicatesCount);
    }

    /**
     * Void writes count of far faculties to the new cell.
     *
     * @param sheet             - current sheet
     * @param farFacultiesCount - count of far schedules
     */
    private void writeFarLessonsCount(XSSFSheet sheet, int farFacultiesCount) {
        Row firstRow = sheet.getRow(0);
        Cell cell = firstRow.createCell(CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER.size() + 2);
        cell.setCellStyle(CellStyleCreator.createMainHeaderCellStyleCharacteristics(sheet.getWorkbook()));
        cell.setCellValue("Виявлена к-ть дисцплін, у яких є проблеми при переїзді: " + farFacultiesCount);
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
        Discipline discipline = this.disciplines.get(disciplineCipher);
        List<Schedule> scheduleForCurrentDisciplineCipher =
                this.schedules.get(disciplineCipher); // full schedule for discipline

        if (scheduleForCurrentDisciplineCipher == null) {
            scheduleForCurrentDisciplineCipher = new ArrayList<>();
            disciplinesWithoutSchedule.add(disciplineCipher);
        }

        String studentGroupCode = getStudentGroup(student);
        student.setCurrentNumberOfPracticeSchedule(0);

        List<Schedule> scheduleForDisciplineAndStudentGroup =
                getScheduleForCurrentUserAndDisciplineCipherWithoutPracticeDuplicates(
                        scheduleForCurrentDisciplineCipher, studentGroupCode, discipline);

        for (Schedule schedule : scheduleForDisciplineAndStudentGroup) {
            LessonType lessonType = schedule.getLessonType();

            // if lesson type is Practice or Laboratory we need to consider max allowed student number per lesson
            if (lessonType.equals(LABORATORY) || lessonType.equals(PRACTICE)) {
                Integer maxHours = getMaxLessonsNumberPerWeek(discipline, lessonType);

                // if the max allowed student number equal to the current number
                // we need to investigate next practice\laboratory lesson for this discipline
                if (maxHours == null && Objects.equals(student.getCurrentNumberOfPracticeSchedule(), maxHours)) {
                    break;
                }
                if (schedule.getNumberOfStudentsInSubGroup() <= schedule.getMaxNumberOfStudentsInSubGroup()) {
                    schedule.setNumberOfStudentsInSubGroup(schedule.getNumberOfStudentsInSubGroup() + 1);
                    student.setCurrentNumberOfPracticeSchedule(student.getCurrentNumberOfPracticeSchedule() + 1);
                }
            }

            ConsolidationOfDisciplinesSchedule consolidationOfDisciplinesSchedule =
                    createConsolidationOfDisciplines(disciplineCipher, student);
            consolidationOfDisciplinesSchedule.setSchedule(schedule);
            schedules.add(consolidationOfDisciplinesSchedule);
        }

        return schedules;
    }

    /**
     * Return group code instead of full group name,
     * for instance, "ПА" instead of "ПА-18"
     *
     * @param student - student with group
     * @return group code
     */
    private String getStudentGroup(Student student) {
        int indexOfLastHyphen = student.getGroup().lastIndexOf("-");
        return student.getGroup().substring(0, indexOfLastHyphen);
    }

    /**
     * Method return schedule for current user and discipline without practice duplicates.
     * Practice duplicates here means that in the input schedule file we have more than 1 time for practice for current group
     *
     * @param scheduleForCurrentDisciplineCipher - schedule that contains all lessons type and can contain duplicates for practice
     * @param studentGroupCode                   - group code for current student
     * @return schedule fot current student and discipline without duplicates
     */
    private List<Schedule> getScheduleForCurrentUserAndDisciplineCipherWithoutPracticeDuplicates(
            List<Schedule> scheduleForCurrentDisciplineCipher, String studentGroupCode, Discipline discipline) {

        // lecture lessons never have any duplicates
        List<Schedule> scheduleWithoutDuplicates = scheduleForCurrentDisciplineCipher.stream()
                .filter(schedule -> schedule.getGroupCodes().contains(studentGroupCode))
                .filter(schedule -> schedule.getLessonType().equals(LECTURE))
                .collect(Collectors.toList());

        // find all practices schedule that contain free places for student
        List<Schedule> practices = scheduleForCurrentDisciplineCipher.stream()
                .filter(schedule -> schedule.getGroupCodes().contains(studentGroupCode))
                .filter(schedule -> !schedule.getLessonType().equals(LECTURE))
                .filter(schedule -> schedule.getNumberOfStudentsInSubGroup() <
                        schedule.getMaxNumberOfStudentsInSubGroup())
                .collect(Collectors.toList());

        if (practices.isEmpty()) {
            return scheduleWithoutDuplicates;
        }

        if (practices.size() == 1) {
            scheduleWithoutDuplicates.add(practices.get(0));
        } else {
            // if practice have 4 time per week => we should add this practice lesson 2 times per week
            Integer maxHours = getMaxLessonsNumberPerWeek(discipline, practices.get(0).getLessonType());
            int toIndex = maxHours > practices.size() ? practices.size() : maxHours;
            IntStream.range(0, toIndex)
                    .forEach(i -> scheduleWithoutDuplicates.add(practices.get(i)));
        }

        return scheduleWithoutDuplicates;
    }

    /**
     * Void return max lessons number for practice or laboratory per week.
     *
     * @param discipline - current discipline
     * @param lessonType - current lesson type (practice or laboratory)
     * @return max number of lessons per week
     * (1 lesson = 2 hours, so to get lessons from hours we should divide hours by 2)
     */
    private Integer getMaxLessonsNumberPerWeek(Discipline discipline, LessonType lessonType) {
        String maxHoursPerLesson = lessonType.equals(LABORATORY) ? discipline.getLaboratoryHoursPerWeek() :
                discipline.getPracticalHoursPerWeek();
        Integer maxHours = StringMapper.parseStringToInt(maxHoursPerLesson);
        if (maxHours != null) {
            maxHours = maxHours <= 2 ? 1 : maxHours / 2;
        }
        return maxHours;
    }

    /**
     * Void create ConsolidationOfDisciplinesSchedule from current student and discipline.
     *
     * @param student          - student for making schedule
     * @param disciplineCipher - cipher of current discipline
     * @return ConsolidationOfDisciplinesSchedule for this student and discipline
     */
    private ConsolidationOfDisciplinesSchedule createConsolidationOfDisciplines(String disciplineCipher,
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
     * Two schedule's rows be considered duplicates for student when:
     * 1. They have the same types of week or one of types - it is a EVERY_WEEK.
     * 2. They have the same days of week.
     * 3. They have the same lesson number.
     * 4. They have different discipline cipher.
     *
     * @param schedules -  full schedule for all students and disciplines that has chosen by students from different faculties
     * @return schedule that contain only duplicated items
     */
    private Set<ConsolidationOfDisciplinesSchedule> getDuplicates(Set<ConsolidationOfDisciplinesSchedule> schedules) {
        Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule = new LinkedHashSet<>();
        schedules.forEach(disciplinesSchedule -> {
            Schedule currentSchedule = disciplinesSchedule.getSchedule();
            filterSchedule(schedules, disciplinesSchedule, currentSchedule)
                    .stream()
                    .filter(consolidation -> consolidation.getSchedule().getScheduleDate().getLessonNumber()
                            .equals(currentSchedule.getScheduleDate().getLessonNumber()))
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
     * Void checks all schedule's rows and mark rows for neighboring lessons as 'Far' when faculties have different types.
     * Two schedule's rows be considered far for student when:
     * 1. They have the same types of week or one of types - it is a EVERY_WEEK.
     * 2. They have the same days of week.
     * 3. They have the neighboring lessons number.
     * 4. They have different faculty types.
     *
     * @param schedules -  full schedule for all students and disciplines that has chosen by students from different faculties
     * @return schedule that contain only far items
     */
    private Set<ConsolidationOfDisciplinesSchedule> getAddressesFar(Set<ConsolidationOfDisciplinesSchedule> schedules) {
        Set<ConsolidationOfDisciplinesSchedule> farAddressesSchedule = new LinkedHashSet<>();
        schedules.forEach(disciplinesSchedule -> {
            Schedule currentSchedule = disciplinesSchedule.getSchedule();
            filterSchedule(schedules, disciplinesSchedule, currentSchedule)
                    .stream()
                    .filter(consolidation -> Math.abs(consolidation.getSchedule().getScheduleDate().getLessonNumber()
                            - currentSchedule.getScheduleDate().getLessonNumber()) == 1)
                    .filter(consolidation -> Math.abs(consolidation.getSchedule().getFacultyType().getType()
                            - currentSchedule.getFacultyType().getType()) > 0)
                    .forEach(consolidation -> {
                        consolidation.setFacultiesFar(true);
                        farAddressesSchedule.add(consolidation);
                    });
        });

        return farAddressesSchedule;
    }

    /**
     * Method filters a schedule by:
     * 1. Student name.
     * 2. Type of week.
     * 3. Day of week.
     *
     * @param schedules           - full schedule for all students and disciplines that has chosen by students from different faculties
     * @param disciplinesSchedule - current disciplines schedule
     * @param currentSchedule     - current schedule
     * @return filtered schedule
     */
    private Set<ConsolidationOfDisciplinesSchedule> filterSchedule(Set<ConsolidationOfDisciplinesSchedule> schedules,
                                                                   ConsolidationOfDisciplinesSchedule disciplinesSchedule,
                                                                   Schedule currentSchedule) {
        return schedules.stream()
                .filter(consolidation -> consolidation.getStudentName()
                        .equals(disciplinesSchedule.getStudentName()))
                .filter(consolidation ->
                        consolidation.getSchedule().getScheduleDate().getTypeOfWeek()
                                .equals(currentSchedule.getScheduleDate().getTypeOfWeek()) ||
                                (consolidation.getSchedule().getScheduleDate().getTypeOfWeek() != WeekType.EVERY_WEEK &&
                                        currentSchedule.getScheduleDate().getTypeOfWeek() == WeekType.EVERY_WEEK) ||
                                (consolidation.getSchedule().getScheduleDate().getTypeOfWeek() == WeekType.EVERY_WEEK &&
                                        currentSchedule.getScheduleDate().getTypeOfWeek() != WeekType.EVERY_WEEK))
                .filter(consolidation -> consolidation.getSchedule().getScheduleDate().getDayOfWeek()
                        .equals(currentSchedule.getScheduleDate().getDayOfWeek()))
                .collect(Collectors.toSet());
    }

    /**
     * Method return cell style that depends on parameters.
     *
     * @param rowIndex     - index of current row for identification this rows like odd or even.
     * @param isDuplicate  - boolean value that shows if the current row is duplicate.
     * @param isFacultyFar - boolean value that shows if the current faculty is far from neighbor.
     *                     <p>
     *                     If isDuplicate = true - the row`s color is orange.
     *                     If isFacultyFar = true - the row`s color is yellow.
     *                     If both parameters = true - the foreground is red.
     * @return cell style.
     */
    private XSSFCellStyle setScheduleForeground(int rowIndex, boolean isDuplicate, boolean isFacultyFar) {
        if (isDuplicate && isFacultyFar) {
            return cellStyles.getDuplicatedAndFarCellStyle();
        } else if (isDuplicate) {
            return cellStyles.getDuplicatedCellStyle();
        } else if (isFacultyFar) {
            return cellStyles.getFarCellStyle();
        }
        return setForeground(rowIndex);
    }

}


