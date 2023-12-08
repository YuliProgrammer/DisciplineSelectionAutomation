package com.discipline.selection.automation.service.dao;

import com.discipline.selection.automation.model.entity.Student;

import java.util.Collection;
import java.util.List;

public interface StudentService {

    /**
     * Method saves all new students and updates all existent students.
     * The existence of students is defined by its unique email.
     * To update the existent one method search students by their emails, and sets the id for all existent students.
     *
     * @param students - all students (can contain new students and already existent)
     * @return a collection of all students, where existent students contains their ids.
     */
    Collection<Student> saveStudents(List<Student> students);

}
