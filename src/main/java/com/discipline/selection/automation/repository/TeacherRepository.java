package com.discipline.selection.automation.repository;

import com.discipline.selection.automation.model.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    Set<Teacher> findAllByNameIn(Collection<String> teacherNames);

}
