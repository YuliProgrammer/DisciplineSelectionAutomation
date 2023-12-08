package com.discipline.selection.automation.repository;

import com.discipline.selection.automation.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {

}
