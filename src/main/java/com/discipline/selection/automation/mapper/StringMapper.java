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
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
