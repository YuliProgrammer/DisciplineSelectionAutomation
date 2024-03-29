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
 * Class for writing  schedule for group or teacher into .xlxs
 *
 * @author Yuliia_Dolnikova
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"oneDisciplineCipher", "dayOfWeek", "lessonNumber"})
@EqualsAndHashCode(of = {"oneDisciplineCipher", "fileName", "groupCodes"})
public class ScheduleByGroupsOrTeachers {

    private String oneDisciplineCipher;
    private FacultyType facultyType;
    private String facultyAddress;
    private WeekDay dayOfWeek;
    private Integer lessonNumber;
    private LessonType lessonType;
    private WeekType typeOfWeek;
    private String fileName;
    private List<String> groupCodes = new ArrayList<>();

}
