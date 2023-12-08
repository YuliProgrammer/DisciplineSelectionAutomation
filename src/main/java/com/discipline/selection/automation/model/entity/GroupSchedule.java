package com.discipline.selection.automation.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "groups_schedule")
@ToString(exclude = "schedule")
public class GroupSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group; // should be stored in column by index 5-7

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // should be stored in column by index 5-7

    public GroupSchedule(GroupSchedule groupSchedule) {
        this.id = groupSchedule.id;
        this.group = new Group(groupSchedule.group);
        this.schedule = groupSchedule.schedule;
    }

}
