package com.discipline.selection.automation.service.writer;

import com.discipline.selection.automation.mapper.StudentMapper;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.writer.created.impl.WriteConsolidationOfDisciplinesScheduleToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.WriteConsolidationOfDisciplinesToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.all.WriteScheduleByGroupsToNewExcelImpl;
import com.discipline.selection.automation.service.writer.created.impl.all.WriteScheduleByTeachersToNewExcelImpl;
import com.discipline.selection.automation.service.writer.existed.impl.WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl;
import com.discipline.selection.automation.service.writer.existed.impl.WriteStudentsCountForDifferentFacultiesToExistedExcelSheetImpl;
import com.discipline.selection.automation.service.writer.existed.impl.WriteStudentsCountToExistedExcelSheetImpl;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.model.enums.LessonType.LABORATORY;
import static com.discipline.selection.automation.model.enums.LessonType.PRACTICE;

public class WriterChainImpl {

    private final Map<String, List<Student>> studentsGroupedByDiscipline;
    private final Map<String, List<Student>> studentsGroupedByGroup;
    private final Map<String, Discipline> disciplines;
    private final Map<String, List<Schedule>> schedule;
    private final Map<String, List<Schedule>> schedulesGroupedByTeacher;
    private final Set<String> disciplinesWithoutSchedule = new LinkedHashSet<>();

    public WriterChainImpl(Map<String, List<Student>> studentsGroupedByGroup,
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
        calculateCurrentStudentsCountForAllDisciplines(disciplines);
    }

    public void write() {

        XSSFWorkbook existedWorkbook = getWorkbook();
        WriterChain writeStudentsCount = new WriteStudentsCountToExistedExcelSheetImpl(studentsGroupedByDiscipline, disciplines, existedWorkbook);
        WriterChain writeDisciplinesForDifferentFacilities =
                new WriteDisciplinesForDifferentFacilitiesToExistedExcelImpl(studentsGroupedByDiscipline, disciplines, existedWorkbook);
        WriterChain writeStudentsCountForDifferentFaculties =
                new WriteStudentsCountForDifferentFacultiesToExistedExcelSheetImpl(studentsGroupedByDiscipline, disciplines, existedWorkbook);

        XSSFWorkbook consolidationWorkbook = getNewWorkbook();
        WriterChain writeConsolidationOfDisciplines =
                new WriteConsolidationOfDisciplinesToNewExcelImpl(studentsGroupedByDiscipline, disciplines, schedule, consolidationWorkbook);
        WriterChain writeConsolidationOfDisciplinesSchedule =
                new WriteConsolidationOfDisciplinesScheduleToNewExcelImpl(studentsGroupedByDiscipline, disciplines,
                        schedule, disciplinesWithoutSchedule, consolidationWorkbook);

        XSSFWorkbook allWorkbook = getNewWorkbook();
        WriterChain writeScheduleByGroups =
                new WriteScheduleByGroupsToNewExcelImpl(studentsGroupedByGroup, schedule, allWorkbook);
        WriterChain writeScheduleByTeachers =
                new WriteScheduleByTeachersToNewExcelImpl(schedulesGroupedByTeacher, allWorkbook);

        writeStudentsCount.setNextWriter(writeDisciplinesForDifferentFacilities);
        writeDisciplinesForDifferentFacilities.setNextWriter(writeStudentsCountForDifferentFaculties);
        writeStudentsCountForDifferentFaculties.setNextWriter(writeConsolidationOfDisciplines);
        writeConsolidationOfDisciplines.setNextWriter(writeConsolidationOfDisciplinesSchedule);
        writeConsolidationOfDisciplinesSchedule.setNextWriter(writeScheduleByGroups);
        writeScheduleByGroups.setNextWriter(writeScheduleByTeachers);

        writeStudentsCount.execute();
    }

    private XSSFWorkbook getWorkbook() {
        try {
            FileInputStream inputStream = new FileInputStream(FILE_NAME);
            return new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private XSSFWorkbook getNewWorkbook() {
        return new XSSFWorkbook();
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

    /**
     * The void calculates the current number of students for all disciplines.
     *
     * @param disciplines - map where key - it is a unique discipline cipher,
     *                    and value - it is a discipline that match to this discipline cipher
     */
    protected void calculateCurrentStudentsCountForAllDisciplines(Map<String, Discipline> disciplines) {
        for (Map.Entry<String, Discipline> disciplineEntry : disciplines.entrySet()) {
            Discipline discipline = disciplineEntry.getValue();
            List<Student> studentsByDiscipline = studentsGroupedByDiscipline.get(disciplineEntry.getKey());
            int studentsCount = studentsByDiscipline == null ? 0 : studentsByDiscipline.size();
            discipline.setStudentsCount(studentsCount);
            disciplines.put(disciplineEntry.getKey(), discipline);
        }
    }

}
