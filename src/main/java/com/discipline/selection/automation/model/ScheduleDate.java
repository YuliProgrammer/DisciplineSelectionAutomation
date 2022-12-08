package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Class for reading data about schedule from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDate {

    private WeekType typeOfWeek;       // should be stored in column by index 5
    private Integer lessonNumber;      // should be stored in column by index 6
    private WeekDay dayOfWeek;         // should be stored in column by index 7

    public ScheduleDate(ScheduleDate scheduleDate) {
        this.typeOfWeek = scheduleDate.typeOfWeek;
        this.lessonNumber = scheduleDate.lessonNumber;
        this.dayOfWeek = scheduleDate.dayOfWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScheduleDate)) return false;
        ScheduleDate that = (ScheduleDate) o;
        return typeOfWeek == that.typeOfWeek && Objects.equals(lessonNumber, that.lessonNumber) && dayOfWeek == that.dayOfWeek;
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeOfWeek, lessonNumber, dayOfWeek);
    }
}
