package com.discipline.selection.automation.service.writer.created.impl;

import com.discipline.selection.automation.model.ConsolidationOfDisciplinesSchedule;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.ScheduleDate;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import com.discipline.selection.automation.service.writer.WriteScheduleForAllWorkDaysToExcel;
import com.discipline.selection.automation.util.CellStyleCreator;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.discipline.selection.automation.model.enums.WeekDay.DAYS;
import static com.discipline.selection.automation.model.enums.WeekType.DENOMINATOR;
import static com.discipline.selection.automation.model.enums.WeekType.EVERY_WEEK;
import static com.discipline.selection.automation.model.enums.WeekType.NUMERATOR;
import static com.discipline.selection.automation.model.enums.WeekType.WEEK_TYPES;
import static com.discipline.selection.automation.util.Constants.EMPTY_VALUE;
import static com.discipline.selection.automation.util.Constants.POSSIBLE_SCHEDULE_CHANGES_SHEET_NAME;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_DATES_HEADER;

public class WritePossibleTimesForDuplicatesLectures extends WriteScheduleForAllWorkDaysToExcel {

    private final Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule;
    private final Set<ConsolidationOfDisciplinesSchedule> consolidationSchedules;
    private Map<String, Set<ScheduleDate>> freeTimeForDiscipline;

    public WritePossibleTimesForDuplicatesLectures(Set<ConsolidationOfDisciplinesSchedule> consolidationSchedules,
                                                   Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule) {

        this.consolidationSchedules = consolidationSchedules;
        this.duplicatedSchedule = duplicatedSchedule;
    }

    @Override
    public void writeToExcel(XSSFWorkbook workbook) {
        initStyles(workbook);
        XSSFSheet sheet = workbook.createSheet(POSSIBLE_SCHEDULE_CHANGES_SHEET_NAME);
        XSSFCellStyle busyCellStyle = CellStyleCreator.createDuplicatedFarCellStyleCharacteristics(workbook);

        this.freeTimeForDiscipline = findFreeTimeForAllDuplicatedDiscipline(duplicatedSchedule);

        writeHeader(sheet);
        writeSchedule(sheet, busyCellStyle);
    }

    /**
     * Void that writes header for the sheet: "Можливі зміни розкладу"
     * That header consists of two parts: the first part - it is basic header titles (day, lesson number and week type)
     * and the second part - it is discipline сiphers that have duplicates in the schedule.
     *
     * @param sheet - current new Excel sheet
     */
    private void writeHeader(XSSFSheet sheet) {
        columnIndex = writeHeader(sheet, SCHEDULE_DATES_HEADER, columnIndex);
        Set<String> header = new LinkedHashSet<>();
        duplicatedSchedule.stream()
                .map(ConsolidationOfDisciplinesSchedule::getDisciplineCipher)
                .sorted()
                .forEach(header::add);
        columnIndex = writeHeader(sheet, header, columnIndex); // write the second part of the header
    }

    @Override
    public List<String> getValuesForAll(WeekDay day, int lessonNumber, WeekType weekType) {
        return freeTimeForDiscipline.values()
                .stream()
                .map(scheduleDates -> {
                    Optional<ScheduleDate> freeDate = scheduleDates.stream()
                            .filter(scheduleDate -> scheduleDate.getDayOfWeek().equals(day)
                                    && scheduleDate.getLessonNumber().equals(lessonNumber)
                                    && scheduleDate.getTypeOfWeek().equals(weekType))
                            .findFirst();
                    return freeDate.isPresent() ? "ТАК" : EMPTY_VALUE;
                }).collect(Collectors.toList());
    }

    /**
     * The method searches for the free dates for all discipline that has duplicates in the resul schedule
     *
     * @param duplicatedSchedule - the duplicates schedule for all students
     * @return the map:
     * the key - is the discipline cipher,
     * the value - is the set of possible free dates for all students,
     * who's chosen this discipline and for all teachers who teachers this discipline
     */
    private Map<String, Set<ScheduleDate>> findFreeTimeForAllDuplicatedDiscipline(Set<ConsolidationOfDisciplinesSchedule> duplicatedSchedule) {

        Map<String, List<ConsolidationOfDisciplinesSchedule>> duplicatesConsolidationByCipher = duplicatedSchedule.stream()
                .collect(Collectors.groupingBy(ConsolidationOfDisciplinesSchedule::getDisciplineCipher));
        Set<String> duplicateDisciplineCiphers = duplicatesConsolidationByCipher.keySet().stream().sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<String, Set<String>> studentsNamesWithDuplicatesByCipher = getFullScheduleForAllStudentsAndDuplicatedDiscipline(
                consolidationSchedules, duplicateDisciplineCiphers);

        // at the beginning of searching - the free dates for each discipline  - it's all possible free dates
        Set<ScheduleDate> scheduleDates = generateAllPossibleDates();
        Map<String, Set<ScheduleDate>> freeTimeForDisciplines = duplicateDisciplineCiphers.stream()
                .collect(Collectors.toMap(cipher -> cipher,
                        cipher -> new LinkedHashSet<>(scheduleDates), (v1, v2) -> v2, LinkedHashMap::new));

        studentsNamesWithDuplicatesByCipher.forEach((cipher, studentNames) -> studentNames.forEach(studentName -> {
            Set<ConsolidationOfDisciplinesSchedule> studentConsolidation = consolidationSchedules.stream()
                    .filter(consolidation -> consolidation.getStudentName().equals(studentName))
                    .collect(Collectors.toSet());
            deleteBusyDatesForDiscipline(freeTimeForDisciplines, cipher, studentConsolidation);
        }));

        duplicateDisciplineCiphers.forEach(cipher -> {
            List<ConsolidationOfDisciplinesSchedule> teachersConsolidation =
                    getTeacherFullConsolidationForTheCurrentDisciplineCipher(cipher);
            deleteBusyDatesForDiscipline(freeTimeForDisciplines, cipher, teachersConsolidation);
        });

        return freeTimeForDisciplines;
    }

    /**
     * The method generates the schedule of each student
     *
     * @param schedulesForAllStudents    - the schedule for all students and all disciplines
     * @param duplicateDisciplineCiphers -the whole set of discipline ciphers that have duplicates in the result schedule
     * @return the map:
     * the key - is the discipline cipher,
     * the value - is the set of students, who has chosen this discipline
     */
    private Map<String, Set<String>> getFullScheduleForAllStudentsAndDuplicatedDiscipline(
            Set<ConsolidationOfDisciplinesSchedule> schedulesForAllStudents,
            Set<String> duplicateDisciplineCiphers) {
        return duplicateDisciplineCiphers.stream()
                .collect(Collectors.toMap(cipher -> cipher, cipher -> schedulesForAllStudents.stream()
                        .filter(c -> c.getDisciplineCipher().equals(cipher))
                        .map(ConsolidationOfDisciplinesSchedule::getStudentName)
                        .collect(Collectors.toSet())));
    }

    /**
     * The method gets the full schedule for each teacher who's teaches the current discipline
     *
     * @param cipher - current discipline cipher
     * @return the whole teacher's schedule of those teachers who teaches the current discipline
     */
    private List<ConsolidationOfDisciplinesSchedule> getTeacherFullConsolidationForTheCurrentDisciplineCipher(String cipher) {
        List<ConsolidationOfDisciplinesSchedule> consolidationForCurrentCipher = consolidationSchedules.stream()
                .filter(consolidation -> consolidation.getDisciplineCipher().equals(cipher))
                .collect(Collectors.toList());

        // find the teachers names, who teaches this discipline
        Set<String> teacherNames = consolidationForCurrentCipher.stream()
                .map(consolidation -> consolidation.getSchedule().getTeacherName())
                .collect(Collectors.toSet());

        // find all schedule for these teachers (one teacher can teach more than 1 discipline)
        return teacherNames.stream()
                .flatMap(teacherName -> consolidationSchedules.stream()
                        .filter(c -> c.getSchedule().getTeacherName().equals(teacherName)))
                .collect(Collectors.toList());
    }

    /**
     * The method deletes all busy dates from the list of possible values for the concrete discipline
     *
     * @param freeTimeForDiscipline - map, where the key - is a discipline cipher,
     *                              and value - is a set of free dates for this discipline
     * @param cipher                - current cipher
     * @param personConsolidation   - the full schedule for the current person
     *                              to which need to find the busy dates and delete them from the @{freeTimeForDiscipline}
     */
    private void deleteBusyDatesForDiscipline(Map<String, Set<ScheduleDate>> freeTimeForDiscipline, String cipher,
                                              Collection<ConsolidationOfDisciplinesSchedule> personConsolidation) {
        Set<ScheduleDate> personBusyDates = getPersonBusyScheduleDates(personConsolidation);
        Set<ScheduleDate> freeScheduleDatesByCipher = freeTimeForDiscipline.get(cipher);
        personBusyDates.forEach(freeScheduleDatesByCipher::remove);
        freeTimeForDiscipline.put(cipher, freeScheduleDatesByCipher);
    }

    /**
     * Methods get all dates when the current person (student or teacher) has any lessons
     *
     * @param personConsolidation - the full schedule for the current person
     * @return dates when the person is busy
     */
    private Set<ScheduleDate> getPersonBusyScheduleDates(Collection<ConsolidationOfDisciplinesSchedule> personConsolidation) {
        return personConsolidation.stream()
                .map(ConsolidationOfDisciplinesSchedule::getSchedule)
                .map(Schedule::getScheduleDate)
                .flatMap(scheduleDate -> {
                    if (!scheduleDate.getTypeOfWeek().equals(EVERY_WEEK)) {
                        return Stream.of(scheduleDate);
                    }
                    List<ScheduleDate> scheduleDatesWithWeekTypes = new ArrayList<>();
                    scheduleDatesWithWeekTypes.add(getScheduleDateWithExactWeekType(scheduleDate, NUMERATOR));
                    scheduleDatesWithWeekTypes.add(getScheduleDateWithExactWeekType(scheduleDate, DENOMINATOR));

                    return scheduleDatesWithWeekTypes.stream();
                })
                .collect(Collectors.toSet());
    }

    /**
     * The method changes the week type from EVERY_WEEK to the exact one
     *
     * @param scheduleDate - date with the week type = EVERY_WEEK
     * @param weekType     - weekType to be changed: NUMERATOR or DENOMINATOR
     * @return new ScheduleDate with the same day, and lesson number, and with updated week type
     */
    private ScheduleDate getScheduleDateWithExactWeekType(ScheduleDate scheduleDate, WeekType weekType) {
        ScheduleDate scheduleDateBasic = new ScheduleDate(scheduleDate);
        scheduleDateBasic.setTypeOfWeek(weekType);
        return scheduleDateBasic;
    }

    /**
     * Method generates all possible dates for lessons
     * The lessons can be held from Monday to Saturday, from the 1st to the 7th, on every week
     *
     * @return set with 84 possible elements (6 week dats * 7 lessons per day * 2 week types)
     */
    private Set<ScheduleDate> generateAllPossibleDates() {
        Set<ScheduleDate> scheduleDates = new LinkedHashSet<>();
        Arrays.stream(DAYS).forEach(day ->
                IntStream.range(1, 8).forEach(lessonNumber ->
                        Arrays.stream(WEEK_TYPES).forEach(weekType ->
                                scheduleDates.add(ScheduleDate.builder()
                                        .dayOfWeek(day)
                                        .lessonNumber(lessonNumber)
                                        .typeOfWeek(weekType)
                                        .build()))));
        return scheduleDates;
    }
}
