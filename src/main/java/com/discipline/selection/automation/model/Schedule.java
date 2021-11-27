package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.LessonType;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.discipline.selection.automation.util.Constants.BLANK_LINE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"typeOfWeek", "lessonNumber", "dayOfWeek", "lessonType", "disciplineCipher", "groupCodes",
        "groupNumber"})
public class Schedule {

    private String disciplineCipher;
    private List<String> groupCodes = new ArrayList<>();
    private Integer maxNumberOfStudentsInSubGroup;
    private Integer numberOfStudentsInSubGroup = 0;
    private String subgroupNumber;
    private String teacherName;
    private WeekType typeOfWeek;
    private String lessonNumber;
    private String dayOfWeek;
    private LessonType lessonType;
    private String groupNumber;

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
    }

    public String scheduleForConsolidationOfDisciplines() {
        return teacherName + BLANK_LINE + dayOfWeek + BLANK_LINE + lessonNumber + BLANK_LINE + typeOfWeek.getName() +
                BLANK_LINE + lessonType.getName();
    }

}
