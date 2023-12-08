package com.discipline.selection.automation.service.dao;

import com.discipline.selection.automation.model.entity.Group;
import com.discipline.selection.automation.model.entity.Schedule;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GroupService {

    void saveUniqueScheduleGroups(List<Schedule> wholeSchedule);

    /**
     * Method saves all new groups and updates all existent groups.
     * The existence of groups is defined by its unique group code.
     * To update the existent one method search groups by their codes, and sets the id for all existent groups.
     *
     * @param groups - all groups (can contain new groups and already existent)
     * @return a collection of all groups, where existent groups contains their ids.
     */
    Collection<Group> saveGroups(Set<Group> groups);

    void fillGroupsByIds(Set<Group> groups);

}
