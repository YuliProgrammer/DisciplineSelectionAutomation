package com.discipline.selection.automation.service.dao.impl;

import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.repository.DisciplineRepository;
import com.discipline.selection.automation.service.dao.DisciplineService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DisciplineServiceImpl implements DisciplineService {

    private final DisciplineRepository disciplineRepository;

    @Override
    public Collection<Discipline> saveDisciplines(Map<String, Discipline> disciplinesByCiphers) {
        Collection<Discipline> disciplines = disciplinesByCiphers.values();
        Map<String, Discipline> existentDisciplinesByCiphers = disciplineRepository.findAllByDisciplineCipherIn(disciplines.stream()
                        .map(Discipline::getDisciplineCipher).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(Discipline::getDisciplineCipher, d -> d));
        disciplinesByCiphers.forEach((disciplineCipher, discipline) -> {
            Discipline existentDiscipline = existentDisciplinesByCiphers.get(disciplineCipher);
            if (Objects.nonNull(existentDiscipline)) {
                discipline.setId(existentDiscipline.getId());
            }
        });
        return disciplineRepository.saveAll(disciplines);
    }

}
