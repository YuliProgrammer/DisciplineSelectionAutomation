package com.discipline.selection.automation.model.entity;

import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.discipline.selection.automation.util.Constants.BLANK_LINE;

/**
 * Class for reading data about schedule from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule")
@EqualsAndHashCode(of = {"scheduleDate", "lessonType", "discipline",
        "groupNumber", "fileName"})
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;   // should be stored in column by index 0

    @Column(name = "lesson_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private LessonType lessonType;    // should be stored in column by index 8

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "schedule", cascade = CascadeType.MERGE)
    private Set<GroupSchedule> groupSchedule;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "schedule_date_id", nullable = false)
    private ScheduleDate scheduleDate; // should be stored in column by index 5-7

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "schedule")
    private Set<TeacherSchedule> teacherSchedules = new HashSet<>();

    @Column(name = "max_students_count")
    private Integer maxNumberOfStudentsInSubGroup;       // should be stored in column by index 2

    @Transient
    private Integer numberOfStudentsInSubGroup = 0;      // should be calculated

    @Column(name = "group_no")
    private Integer groupNumber;       // should be stored in column by index 9

    @Column(name = "subgroup_no")
    private Integer subgroupNumber;    // should be stored in column by index 3

    @Column(name = "facility_type", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private FacultyType facultyType;  // should be stored in column by index 10

    @Column(name = "facility_address")
    private String facultyAddress;    // should be stored in column by index 11

    @Transient
    private String fileName;          // name of file where info was stored

    public Schedule(Schedule schedule) {
        this.id = schedule.id;
        this.discipline = schedule.discipline;
        this.groupSchedule = schedule.groupSchedule.stream()
                .map(GroupSchedule::new).collect(Collectors.toSet());
        this.maxNumberOfStudentsInSubGroup = schedule.maxNumberOfStudentsInSubGroup;
        this.numberOfStudentsInSubGroup = schedule.numberOfStudentsInSubGroup;
        this.subgroupNumber = schedule.subgroupNumber;
        this.teacherSchedules = schedule.teacherSchedules.stream()
                .map(TeacherSchedule::new).collect(Collectors.toSet());
        this.scheduleDate = new ScheduleDate(schedule.scheduleDate);
        this.lessonType = schedule.lessonType;
        this.groupNumber = schedule.groupNumber;
        this.facultyType = schedule.facultyType;
        this.facultyAddress = schedule.facultyAddress;
        this.fileName = schedule.fileName;
    }

    public String scheduleForConsolidationOfDisciplines() {
        ScheduleDate scheduleDate = this.scheduleDate;
        String teacherNames = this.teacherSchedules.stream()
                .map(TeacherSchedule::getTeacher).map(Teacher::getName)
                .collect(Collectors.joining(", "));
        return teacherNames + BLANK_LINE + scheduleDate.getDayOfWeek().getName() + BLANK_LINE +
                scheduleDate.getLessonNumber() + BLANK_LINE +
                scheduleDate.getTypeOfWeek().getName() + BLANK_LINE + lessonType.getName();
    }

}
