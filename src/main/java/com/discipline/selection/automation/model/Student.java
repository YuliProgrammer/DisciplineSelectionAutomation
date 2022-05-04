package com.discipline.selection.automation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class for reading data about student from .xlsx
 *
 * @author Yuliia_Dolnikova
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private String facilityCipher;
    private String email;
    private String name;
    private String course;
    private String group;
    private String disciplinesNumber;
    private Integer currentNumberOfPracticeSchedule;
    private Discipline discipline = new Discipline();

}
