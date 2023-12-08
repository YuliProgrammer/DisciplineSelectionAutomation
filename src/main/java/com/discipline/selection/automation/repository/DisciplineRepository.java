package com.discipline.selection.automation.repository;

import com.discipline.selection.automation.model.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Set;

public interface DisciplineRepository extends JpaRepository<Discipline, Integer> {

    Set<Discipline> findAllByDisciplineCipherIn(Collection<String> disciplineCiphers);

}
