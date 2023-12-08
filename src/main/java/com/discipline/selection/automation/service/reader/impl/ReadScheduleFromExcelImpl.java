package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.ScheduleMapper;
import com.discipline.selection.automation.model.GroupedSchedule;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Group;
import com.discipline.selection.automation.model.entity.GroupSchedule;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.ScheduleDate;
import com.discipline.selection.automation.model.entity.Teacher;
import com.discipline.selection.automation.model.entity.TeacherSchedule;
import com.discipline.selection.automation.repository.GroupRepository;
import com.discipline.selection.automation.repository.GroupScheduleRepository;
import com.discipline.selection.automation.repository.ScheduleDateRepository;
import com.discipline.selection.automation.repository.ScheduleRepository;
import com.discipline.selection.automation.repository.TeacherRepository;
import com.discipline.selection.automation.repository.TeacherScheduleRepository;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.MainApplication.SCHEDULE_FILE_NAMES;
import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.SCHEDULE_SHEET_INDEX;
import static com.discipline.selection.automation.util.Constants.TEACHER;

@Service
@AllArgsConstructor
public class ReadScheduleFromExcelImpl extends BasicExcelReaderChain<String, Map<String, List<Schedule>>> {

    private static String FILE_NAME;

    private final GroupRepository groupRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    private final TeacherRepository teacherRepository;
    private final TeacherScheduleRepository teacherScheduleRepository;
    private final ScheduleDateRepository scheduleDateRepository;

    private final ScheduleRepository scheduleRepository;

    @Override
    public Map<String, Map<String, List<Schedule>>> uploadData() {
        Map<String, Map<String, List<Schedule>>> groupedSchedulesMap = new HashMap<>();
        Map<String, List<Schedule>> disciplineMap;
        Map<String, List<Schedule>> teacherMap;

        for (String scheduleFiles : SCHEDULE_FILE_NAMES) {
            FILE_NAME = scheduleFiles;
            try (FileInputStream file = new FileInputStream(FILE_NAME)) {
                Workbook workbook = new XSSFWorkbook(file);
                GroupedSchedule groupedSchedule = getSchedule(workbook);

                disciplineMap = groupedSchedulesMap.containsKey(DISCIPLINE) ?
                        groupedSchedulesMap.get(DISCIPLINE) : new HashMap<>();
                teacherMap = groupedSchedulesMap.containsKey(TEACHER) ?
                        groupedSchedulesMap.get(TEACHER) : new HashMap<>();

                disciplineMap.putAll(groupedSchedule.getSchedulesGroupedByDisciplineCipher());

                Map<String, List<Schedule>> schedulesGroupedByTeacher = groupedSchedule.getSchedulesGroupedByTeacher();
                for (Map.Entry<String, List<Schedule>> entry : schedulesGroupedByTeacher.entrySet()) {
                    setScheduleByKey(teacherMap, entry.getKey(), entry.getValue());
                }

                groupedSchedulesMap.put(DISCIPLINE, disciplineMap);
                groupedSchedulesMap.put(TEACHER, teacherMap);

                incomingDataDto.setSchedulesGroupedByDisciplineCipher(disciplineMap);
                incomingDataDto.setSchedulesGroupedByTeacher(teacherMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return groupedSchedulesMap;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return 2 maps:
     * 1. map where key - it is a unique discipline cipher,
     * and value - it is a schedule for this discipline cipher
     * 2. map where key - it is a unique teacher name,
     * and value - it is a schedule for this teacher
     */
    @Transactional
    public GroupedSchedule getSchedule(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(SCHEDULE_SHEET_INDEX);
        Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher = new LinkedHashMap<>();
        Map<String, List<Schedule>> schedulesGroupedByTeacher = new LinkedHashMap<>();
        List<Schedule> wholeSchedule = new ArrayList<>();

        for (int rowIndex = 1; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.isEmpty() || rowData.get(0).isEmpty()) {
                break;
            }

            List<Schedule> schedulesFromCurrentRow =
                    ScheduleMapper.mapRowDataToSchedule(rowData, rowIndex, FILE_NAME);
            // replace discipline object which contains only cipher and name by object
            // which contains all information about discipline
            schedulesFromCurrentRow.forEach(schedule -> schedule.getDiscipline().setId(incomingDataDto.getDisciplines()
                    .get(schedule.getDiscipline().getDisciplineCipher()).getId()));
            wholeSchedule.addAll(schedulesFromCurrentRow);

            // fill maps
            String disciplineCipher = schedulesFromCurrentRow.get(0).getDiscipline().getDisciplineCipher();
            setScheduleByKey(schedulesGroupedByDisciplineCipher, disciplineCipher, schedulesFromCurrentRow);

            schedulesFromCurrentRow.stream().flatMap(st -> st.getTeacherSchedules().stream())
                    .forEach(teacherSchedule -> setScheduleByKey(schedulesGroupedByTeacher,
                            teacherSchedule.getTeacher().getName(), schedulesFromCurrentRow));
        }
        System.out.println("\nРозклад було успішно зчитано з файлу");

        saveUniqueScheduleDates(wholeSchedule);
        List<Schedule> savedSchedule = scheduleRepository.saveAll(wholeSchedule);

        saveUniqueScheduleGroups(savedSchedule);
        saveUniqueScheduleTeachers(savedSchedule);

        System.out.println("Розклад було успішно збережено в базі даних");

        return GroupedSchedule.builder()
                .schedulesGroupedByDisciplineCipher(schedulesGroupedByDisciplineCipher)
                .schedulesGroupedByTeacher(schedulesGroupedByTeacher)
                .build();
    }

    private void saveUniqueScheduleDates(List<Schedule> wholeSchedule) {
        Set<ScheduleDate> scheduleDates = wholeSchedule.stream().map(Schedule::getScheduleDate).collect(Collectors.toSet());
        scheduleDateRepository.saveAll(scheduleDates);

        Map<ScheduleDate, Integer> scheduleDateIds = scheduleDates.stream()
                .collect(Collectors.toMap(sd -> sd, ScheduleDate::getId));
        wholeSchedule.forEach(schedule -> schedule.getScheduleDate().setId(scheduleDateIds.get(schedule.getScheduleDate())));
    }

    private void saveUniqueScheduleGroups(List<Schedule> wholeSchedule) {
        // fill study_groups
        Set<Group> groups = wholeSchedule.stream().flatMap(schedule -> schedule.getGroupSchedule().stream())
                .map(GroupSchedule::getGroup).collect(Collectors.toSet());
        groupRepository.saveAll(groups);

        // fill groups_schedule table
        Map<Group, Group> groupIds = groups.stream()
                .collect(Collectors.toMap(sd -> sd, g -> g));
        Set<GroupSchedule> groupSchedules = new HashSet<>();
        wholeSchedule.forEach(schedule -> schedule.getGroupSchedule().forEach(groupSchedule ->
                groupSchedules.add(GroupSchedule.builder()
                        .group(groupIds.get(groupSchedule.getGroup())).schedule(schedule)
                        .build())));
        groupScheduleRepository.saveAll(groupSchedules);
    }

    private void saveUniqueScheduleTeachers(List<Schedule> wholeSchedule) {
        // fill teachers & teachers_disciplines tables
        Set<Teacher> teachers = wholeSchedule.stream()
                .flatMap(schedule -> schedule.getTeacherSchedules().stream())
                .map(TeacherSchedule::getTeacher).collect(Collectors.toSet());

        Set<TeacherSchedule> teacherSchedules = new HashSet<>();

        teachers.forEach(teacher -> wholeSchedule.forEach(schedule -> {
            if (schedule.getTeacherSchedules().stream().map(TeacherSchedule::getTeacher)
                    .anyMatch(t -> t.equals(teacher))) {
                Set<Discipline> teacherDisciplines = teacher.getDisciplines();
                teacherDisciplines = Objects.isNull(teacherDisciplines) ? new HashSet<>() : teacherDisciplines;
                teacherDisciplines.add(schedule.getDiscipline());
                teacher.setDisciplines(teacherDisciplines);

                teacherSchedules.add(TeacherSchedule.builder().teacher(teacher).schedule(schedule).build());
            }
        }));

        List<Teacher> savedTeachers = teacherRepository.saveAll(teachers);

        // fill teachers_schedule
        teacherScheduleRepository.saveAll(teacherSchedules);

        Map<Teacher, Teacher> teacherIds = savedTeachers.stream()
                .collect(Collectors.toMap(sd -> sd, t -> t));
        wholeSchedule.forEach(schedule -> schedule.getTeacherSchedules()
                .forEach(teacherSchedule -> teacherSchedule.setTeacher(teacherIds.get(teacherSchedule.getTeacher()))));
    }

    private void setScheduleByKey(Map<String, List<Schedule>> schedulesGroupedByKey, String key,
                                  List<Schedule> schedulesFromCurrentRow) {
        List<Schedule> schedulesByDisciplineCipher = schedulesGroupedByKey.get(key);
        List<Schedule> schedules =
                (schedulesByDisciplineCipher == null) ? new ArrayList<>() : schedulesByDisciplineCipher;

        schedules.addAll(schedulesFromCurrentRow);
        schedulesGroupedByKey.put(key, schedules);
    }
}
