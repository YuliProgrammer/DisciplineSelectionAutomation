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
@Table(name = "teachers_schedule")
@ToString(exclude = "schedule")
public class TeacherSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher; // should be stored in column by index 5-7

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule; // should be stored in column by index 5-7

    public TeacherSchedule(TeacherSchedule teacherSchedule) {
        this.id = teacherSchedule.id;
        this.teacher = new Teacher(teacherSchedule.getTeacher());
        this.schedule = teacherSchedule.getSchedule();
    }
}
