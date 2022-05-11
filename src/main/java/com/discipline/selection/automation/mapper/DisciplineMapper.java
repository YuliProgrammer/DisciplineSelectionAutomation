package com.discipline.selection.automation.mapper;

import com.discipline.selection.automation.model.Discipline;
import lombok.experimental.UtilityClass;

import java.util.Map;

import static com.discipline.selection.automation.util.Constants.NUMBER_OF_STUDENTS_IN_FLOW;
import static com.discipline.selection.automation.util.Constants.NUMBER_OF_STUDENTS_IN_GROUP;
import static com.discipline.selection.automation.util.DisciplineCipherEngToUa.replaceEngByUa;

/**
 * Class creates Discipline object from Excel rows data
 *
 * @author Yuliia_Dolnikova
 */
@UtilityClass
public class DisciplineMapper {

    /**
     * @param rowData - map where key - it is a column index and value - it is column data
     * @return discipline
     */
    public Discipline mapRowDataToDiscipline(Map<Integer, String> rowData) {
        Discipline discipline = new Discipline();

        for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
            String value = entry.getValue().trim();
            switch (entry.getKey()) {
                case 0:
                    discipline.setDisciplineCipher(replaceEngByUa(value));
                    break;
                case 1:
                    discipline.setDisciplineName(value);
                    break;
                case 2:
                    discipline.setFacilityCipher(value);
                    break;
                case 3:
                    discipline.setCathedraCipher(value);
                    break;
                case 4:
                    discipline.setLecturesHoursPerWeek(value);
                    break;
                case 5:
                    discipline.setPracticalHoursPerWeek(value);
                    break;
                case 6:
                    discipline.setLaboratoryHoursPerWeek(value);
                    break;
                case 7:
                    Integer studentsFlowCount = StringMapper.parseStringToInt(value);
                    discipline.setNumberOfStudentsInFlow(
                            (studentsFlowCount == null || studentsFlowCount <= 0) ? NUMBER_OF_STUDENTS_IN_FLOW :
                                    studentsFlowCount);
                    break;
                case 8:
                    Integer studentsGroupCount = StringMapper.parseStringToInt(value);
                    discipline.setNumberOfStudentsInGroup(
                            (studentsGroupCount == null || studentsGroupCount <= 0) ? NUMBER_OF_STUDENTS_IN_GROUP :
                                    studentsGroupCount);
                    break;
                case 9:
                    discipline.setNumberOfStudentsInSubGroup(StringMapper.parseStringToInt(value));
                    break;
                case 10:
                    discipline.setMaxStudentsCount(StringMapper.parseStringToInt(value));
                    break;
            }
        }

        return discipline;
    }

}
