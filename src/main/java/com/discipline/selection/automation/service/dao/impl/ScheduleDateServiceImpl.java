package com.discipline.selection.automation.service.dao.impl;

import com.discipline.selection.automation.model.entity.ScheduleDate;
import com.discipline.selection.automation.repository.ScheduleDateRepository;
import com.discipline.selection.automation.service.dao.ScheduleDateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleDateServiceImpl implements ScheduleDateService {

    private final ScheduleDateRepository scheduleDateRepository;

    @Override
    public Collection<ScheduleDate> saveScheduleDates(Set<ScheduleDate> scheduleDates) {
        Map<ScheduleDate, ScheduleDate> existentGroupsByCiphers = scheduleDateRepository.findAll().stream()
                .collect(Collectors.toMap(sd -> sd, sd -> sd));

        scheduleDates.forEach(scheduleDate -> {
            ScheduleDate existentScheduleDate = existentGroupsByCiphers.get(scheduleDate);
            if (Objects.nonNull(existentScheduleDate)) {
                scheduleDate.setId(existentScheduleDate.getId());
            }
        });

        return scheduleDateRepository.saveAll(scheduleDates);
    }

}
