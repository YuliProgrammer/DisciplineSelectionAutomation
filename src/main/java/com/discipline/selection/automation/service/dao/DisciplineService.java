package com.discipline.selection.automation.service.dao;

import com.discipline.selection.automation.model.entity.Discipline;

import java.util.Collection;
import java.util.Map;

public interface DisciplineService {

    /**
     * Method saves all new disciplines and updates all existent disciplines.
     * The existence of discipline is defined by its unique discipline cipher.
     * To update the existent one method search disciplines by their ciphers, and sets the id for all existent disciplines.
     *
     * @param disciplinesByCiphers - all disciplines grouped by their ciphers (map can contain new disciplines and already existent)
     * @return a collection of all disciplines, where existent disciplines contains their ids.
     */
    Collection<Discipline> saveDisciplines(Map<String, Discipline> disciplinesByCiphers);

}
