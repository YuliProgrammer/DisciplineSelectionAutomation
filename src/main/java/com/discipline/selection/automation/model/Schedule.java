package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.LessonType;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.discipline.selection.automation.util.Constants.BLANK_LINE;

/**
 * Class for reading from .xlxs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"typeOfWeek", "lessonNumber", "dayOfWeek", "lessonType", "disciplineCipher", "groupCodes",
        "groupNumber", "fileName"})
public class Schedule {

    private String disciplineCipher;   // should be stored in column by index 0
    private List<String> groupCodes = new ArrayList<>(); // should be stored in column by index 1
    private Integer maxNumberOfStudentsInSubGroup;       // should be stored in column by index 2
    private Integer numberOfStudentsInSubGroup = 0;      // should be calculated
    private String subgroupNumber;    // should be stored in column by index 3
    private String teacherName;       // should be stored in column by index 4
    private WeekType typeOfWeek;      // should be stored in column by index 5
    private Integer lessonNumber;      // should be stored in column by index 6
    private WeekDay dayOfWeek;         // should be stored in column by index 7
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
        this.typeOfWeek = schedule.typeOfWeek;
        this.lessonNumber = schedule.lessonNumber;
        this.dayOfWeek = schedule.dayOfWeek;
        this.lessonType = schedule.lessonType;
        this.groupNumber = schedule.groupNumber;
        this.facultyType = schedule.facultyType;
        this.facultyAddress = schedule.facultyAddress;
        this.fileName = schedule.fileName;
    }

    public String scheduleForConsolidationOfDisciplines() {
        return teacherName + BLANK_LINE + dayOfWeek.getName() + BLANK_LINE + lessonNumber + BLANK_LINE + typeOfWeek.getName() +
                BLANK_LINE + lessonType.getName();
    }

}
