package com.discipline.selection.automation.service.dao;

import com.discipline.selection.automation.model.entity.ScheduleDate;

import java.util.Collection;
import java.util.Set;

public interface ScheduleDateService {

    /**
     * Method saves all new scheduleDates and updates all existent scheduleDates.
     * The existence of scheduleDates is defined by the combination of 3 fields: typeOfWeek+dayOfWeek+lessonNumber.
     * To update the existent one method search scheduleDates by these fields, and sets the id for all existent scheduleDates.
     *
     * @param scheduleDates - all scheduleDates (can contain new scheduleDates and already existent)
     * @return a collection of all scheduleDates, where existent scheduleDates contains their ids.
     */
    Collection<ScheduleDate> saveScheduleDates(Set<ScheduleDate> scheduleDates);

}
