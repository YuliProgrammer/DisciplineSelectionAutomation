package com.discipline.selection.automation.mapper;

import lombok.experimental.UtilityClass;

/**
 * Class parse string value to int
 *
 * @author Yuliia_Dolnikova
 */
@UtilityClass
public class StringMapper {

    public Integer parseStringToInt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
