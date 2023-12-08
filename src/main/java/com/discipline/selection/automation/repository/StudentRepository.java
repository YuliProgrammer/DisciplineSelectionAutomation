package com.discipline.selection.automation.repository;

import com.discipline.selection.automation.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, Integer> {

    Set<Student> findAllByEmailIn(Set<String> studentEmail);

}
