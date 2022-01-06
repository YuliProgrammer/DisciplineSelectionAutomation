package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.FacultyType;
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
    private WeekDay dayOfWeek;                  // should be stored in column by index 0
    private Integer lessonNumber;              // should be stored in column by index 1
    private WeekType typeOfWeek;               // should be stored in column by index 2
    private List<String> disciplineCiphers;    // should be stored in column by index 3

    public List<String> getValuesForScheduleByGroups() {
        List<String> valuesForScheduleByGroups = new ArrayList<>();
        valuesForScheduleByGroups.add(dayOfWeek.getName());
        valuesForScheduleByGroups.add(lessonNumber.toString());
        valuesForScheduleByGroups.add(typeOfWeek.getName());
        valuesForScheduleByGroups.add(oneDisciplineCipher);
        return valuesForScheduleByGroups;
    }

}
