package com.discipline.selection.automation;

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
@ToString
@EqualsAndHashCode
public class ScheduleByTeachers {

    private String disciplineCipher;
    private String facultyAddress;

}
