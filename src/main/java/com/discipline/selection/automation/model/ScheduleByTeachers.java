package com.discipline.selection.automation.model;

import com.discipline.selection.automation.model.enums.LessonType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class for writing in .xlxs
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ScheduleByTeachers {

    private String disciplineCipher;
    private String facultyAddress;
    private LessonType lessonType;
    private String fileName;
}
