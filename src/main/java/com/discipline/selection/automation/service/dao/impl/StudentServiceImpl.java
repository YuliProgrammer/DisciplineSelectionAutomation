package com.discipline.selection.automation.service.dao.impl;

import com.discipline.selection.automation.model.entity.Group;
import com.discipline.selection.automation.model.entity.Student;
import com.discipline.selection.automation.repository.GroupRepository;
import com.discipline.selection.automation.repository.StudentRepository;
import com.discipline.selection.automation.service.dao.GroupService;
import com.discipline.selection.automation.service.dao.StudentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final GroupService groupService;
    private final GroupRepository groupRepository;

    @Override
    public Collection<Student> saveStudents(List<Student> students) {
        Map<String, Student> existentStudentsByEmail = studentRepository.findAllByEmailIn(students.stream()
                        .map(Student::getEmail).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(Student::getEmail, s -> s));

        groupService.saveGroups(students.stream().map(Student::getGroup).collect(Collectors.toSet()));
        Map<String, Group> allByGroupCodeIn = groupRepository.findAllByGroupCodeIn(students.stream()
                        .map(Student::getGroup).map(Group::getGroupCode).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Group::getGroupCode, g -> g));
        //honchare@fsnmv.dnu.edu.ua
        students.forEach(student -> {
            student.setGroup(allByGroupCodeIn.get(student.getGroup().getGroupCode()));
            Student existentStudent = existentStudentsByEmail.get(student.getEmail());
            if (Objects.nonNull(existentStudent)) {
                student.setId(existentStudent.getId());
            }
        });
        return studentRepository.saveAll(students);
    }

}
