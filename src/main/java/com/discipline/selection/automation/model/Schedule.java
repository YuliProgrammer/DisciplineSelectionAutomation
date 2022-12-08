package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.discipline.selection.automation.util.Constants.BLANK_LINE;

/**
 * Class for reading data about schedule from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"scheduleDate", "lessonType", "disciplineCipher", "groupCodes",
        "groupNumber", "fileName"})
public class Schedule {

    private String disciplineCipher;   // should be stored in column by index 0
    private List<String> groupCodes = new ArrayList<>(); // should be stored in column by index 1
    private Integer maxNumberOfStudentsInSubGroup;       // should be stored in column by index 2
    private Integer numberOfStudentsInSubGroup = 0;      // should be calculated
    private String subgroupNumber;    // should be stored in column by index 3
    private String teacherName;       // should be stored in column by index 4
    private ScheduleDate scheduleDate; // should be stored in column by index 5-7
    private LessonType lessonType;    // should be stored in column by index 8
    private String groupNumber;       // should be stored in column by index 9
    private FacultyType facultyType;  // should be stored in column by index 10
    private String facultyAddress;    // should be stored in column by index 11
    private String fileName;          // name of file where info was stored

    public Schedule(Schedule schedule) {
        this.disciplineCipher = schedule.disciplineCipher;
        this.groupCodes.addAll(schedule.groupCodes);
        this.maxNumberOfStudentsInSubGroup = schedule.maxNumberOfStudentsInSubGroup;
        this.subgroupNumber = schedule.subgroupNumber;
        this.teacherName = schedule.teacherName;
        this.scheduleDate = new ScheduleDate(schedule.scheduleDate);
        this.lessonType = schedule.lessonType;
        this.groupNumber = schedule.groupNumber;
        this.facultyType = schedule.facultyType;
        this.facultyAddress = schedule.facultyAddress;
        this.fileName = schedule.fileName;
    }

    public String scheduleForConsolidationOfDisciplines() {
        return teacherName + BLANK_LINE + scheduleDate.getDayOfWeek().getName() + BLANK_LINE +
                scheduleDate.getLessonNumber() + BLANK_LINE +
                scheduleDate.getTypeOfWeek().getName() + BLANK_LINE + lessonType.getName();
    }

}
