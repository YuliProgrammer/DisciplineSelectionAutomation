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
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for writing in .xlxs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"oneDisciplineCipher", "dayOfWeek", "lessonNumber"})
@EqualsAndHashCode(of = {"oneDisciplineCipher"})
public class ScheduleByGroupsOrTeachers {

    private String oneDisciplineCipher;
    private FacultyType facultyType;
    private String facultyAddress;
    private WeekDay dayOfWeek;
    private Integer lessonNumber;
    private LessonType lessonType;
    private WeekType typeOfWeek;
    private List<String> groupCodes = new ArrayList<>();

    public List<String> getValuesForScheduleByGroups() {
        List<String> valuesForScheduleByGroups = new ArrayList<>();
        valuesForScheduleByGroups.add(dayOfWeek.getName());
        valuesForScheduleByGroups.add(lessonNumber.toString());
        valuesForScheduleByGroups.add(typeOfWeek.getName());
        valuesForScheduleByGroups.add(oneDisciplineCipher);
        return valuesForScheduleByGroups;
    }

}
