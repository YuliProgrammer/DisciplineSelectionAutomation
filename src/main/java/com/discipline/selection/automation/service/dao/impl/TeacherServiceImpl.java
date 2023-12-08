package com.discipline.selection.automation.service.dao.impl;

import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.Teacher;
import com.discipline.selection.automation.model.entity.TeacherSchedule;
import com.discipline.selection.automation.repository.TeacherRepository;
import com.discipline.selection.automation.repository.TeacherScheduleRepository;
import com.discipline.selection.automation.service.dao.TeacherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final TeacherScheduleRepository teacherScheduleRepository;

    @Override
    public void saveUniqueScheduleTeachers(List<Schedule> wholeSchedule) {
        Set<Teacher> teachers = wholeSchedule.stream()
                .flatMap(schedule -> schedule.getTeacherSchedules().stream())
                .map(TeacherSchedule::getTeacher).collect(Collectors.toSet());

        Set<TeacherSchedule> teacherSchedules = new HashSet<>();
        teachers.forEach(teacher -> wholeSchedule.forEach(schedule -> {
            if (schedule.getTeacherSchedules().stream().map(TeacherSchedule::getTeacher)
                    .anyMatch(t -> t.equals(teacher))) {
                // fill teachers & teachers_disciplines tables
                Set<Discipline> teacherDisciplines = teacher.getDisciplines();
                teacherDisciplines = Objects.isNull(teacherDisciplines) ? new HashSet<>() : teacherDisciplines;
                teacherDisciplines.add(schedule.getDiscipline());
                teacher.setDisciplines(teacherDisciplines);

                // fill teachers_schedule
                teacherSchedules.add(TeacherSchedule.builder().teacher(teacher).schedule(schedule).build());
            }
        }));

        Collection<Teacher> savedTeachers = saveTeachers(teachers);

        // fill teachers_schedule
        teacherScheduleRepository.saveAll(teacherSchedules);
        Map<Teacher, Teacher> teacherIds = savedTeachers.stream()
                .collect(Collectors.toMap(sd -> sd, t -> t));
        wholeSchedule.forEach(schedule -> schedule.getTeacherSchedules()
                .forEach(teacherSchedule -> teacherSchedule.setTeacher(teacherIds.get(teacherSchedule.getTeacher()))));
    }

    @Override
    public Collection<Teacher> saveTeachers(Set<Teacher> teachers) {
        Map<String, Teacher> existentGroupsByCiphers = teacherRepository.findAllByNameIn(teachers.stream()
                        .map(Teacher::getName).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(Teacher::getName, d -> d));

        teachers.forEach(teacher -> {
            Teacher existentTeacher = existentGroupsByCiphers.get(teacher.getName());
            if (Objects.nonNull(existentTeacher)) {
                teacher.setId(existentTeacher.getId());
            }
        });

        return teacherRepository.saveAll(teachers);
    }

}
