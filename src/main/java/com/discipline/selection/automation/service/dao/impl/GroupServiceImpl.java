package com.discipline.selection.automation.service.dao.impl;

import com.discipline.selection.automation.model.entity.Group;
import com.discipline.selection.automation.model.entity.GroupSchedule;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.repository.GroupRepository;
import com.discipline.selection.automation.repository.GroupScheduleRepository;
import com.discipline.selection.automation.service.dao.GroupService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupScheduleRepository groupScheduleRepository;

    @Override
    public void saveUniqueScheduleGroups(List<Schedule> wholeSchedule) {
        // fill study_groups
        Set<Group> groupsWithoutIds = wholeSchedule.stream().flatMap(schedule -> schedule.getGroupSchedule().stream())
                .map(GroupSchedule::getGroup).collect(Collectors.toSet());
        Collection<Group> groups = saveGroups(groupsWithoutIds);

        // fill groups_schedule table
        Map<Group, Group> groupIds = groups.stream()
                .collect(Collectors.toMap(sd -> sd, g -> g));
        Set<GroupSchedule> groupSchedules = new HashSet<>();
        wholeSchedule.forEach(schedule -> schedule.getGroupSchedule().forEach(groupSchedule ->
                groupSchedules.add(GroupSchedule.builder()
                        .group(groupIds.get(groupSchedule.getGroup())).schedule(schedule)
                        .build())));
        groupScheduleRepository.saveAll(groupSchedules);
    }

    @Override
    public Collection<Group> saveGroups(Set<Group> groups) {
        fillGroupsByIds(groups);
        return groupRepository.saveAll(groups);
    }

    @Override
    public void fillGroupsByIds(Set<Group> groups) {
        Map<String, Group> existentGroupsByCiphers = groupRepository.findAllByGroupCodeIn(groups.stream()
                        .map(Group::getGroupCode).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(Group::getGroupCode, d -> d));

        groups.forEach(group -> {
            Group existentGroup = existentGroupsByCiphers.get(group.getGroupCode());
            if (Objects.nonNull(existentGroup)) {
                group.setId(existentGroup.getId());
            }
        });
    }

}
