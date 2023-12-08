package com.discipline.selection.automation.service.dao;

import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.Teacher;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface TeacherService {

    void saveUniqueScheduleTeachers(List<Schedule> wholeSchedule);

    /**
     * Method saves all new teachers and updates all existent teachers.
     * The existence of teachers is defined by its unique names.
     * To update the existent one method search teachers by their names, and sets the id for all existent teachers.
     *
     * @param teachers - all teachers (can contain new teachers and already existent)
     * @return a collection of all teachers, where existent teachers contains their ids.
     */
     Collection<Teacher> saveTeachers(Set<Teacher> teachers);

}
