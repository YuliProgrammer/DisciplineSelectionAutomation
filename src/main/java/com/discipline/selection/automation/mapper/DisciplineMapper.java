package com.discipline.selection.automation.mapper;

import com.discipline.selection.automation.model.entity.Discipline;
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
                    discipline.setLecturesHoursPerWeek(convertStringToNumber(value, 0));
                    break;
                case 5:
                    discipline.setPracticalHoursPerWeek(convertStringToNumber(value, 0));
                    break;
                case 6:
                    discipline.setLaboratoryHoursPerWeek(convertStringToNumber(value, 0));
                    break;
                case 7:
                    discipline.setNumberOfStudentsInFlow(convertStringToNumber(value, NUMBER_OF_STUDENTS_IN_FLOW));
                    break;
                case 8:
                    discipline.setNumberOfStudentsInGroup(convertStringToNumber(value, NUMBER_OF_STUDENTS_IN_GROUP));
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


    private Integer convertStringToNumber(String value, Integer defaultValue) {
        Integer parsedValue = StringMapper.parseStringToInt(value);
        return (parsedValue == null || parsedValue <= 0) ? defaultValue : parsedValue;
    }

}
