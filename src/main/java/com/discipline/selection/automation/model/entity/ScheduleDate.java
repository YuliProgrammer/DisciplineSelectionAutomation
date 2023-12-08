package com.discipline.selection.automation.model.entity;

import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class for reading data about schedule from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule_date")
@EqualsAndHashCode(exclude = "id")
public class ScheduleDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "week_type", nullable = false, length = 11)
    @Enumerated(EnumType.STRING)
    private WeekType typeOfWeek;       // should be stored in column by index 5

    @Column(name = "week_day", nullable = false, length = 9)
    @Enumerated(EnumType.STRING)
    private WeekDay dayOfWeek;         // should be stored in column by index 7

    @Column(name = "lesson", nullable = false)
    private Integer lessonNumber;      // should be stored in column by index 6

    public ScheduleDate(ScheduleDate scheduleDate) {
        this.id = scheduleDate.id;
        this.typeOfWeek = scheduleDate.typeOfWeek;
        this.lessonNumber = scheduleDate.lessonNumber;
        this.dayOfWeek = scheduleDate.dayOfWeek;
    }

}
