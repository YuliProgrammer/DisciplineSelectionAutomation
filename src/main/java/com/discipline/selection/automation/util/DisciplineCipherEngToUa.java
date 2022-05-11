package com.discipline.selection.automation.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DisciplineCipherEngToUa {

    public static String replaceEngByUa(String value) {
        return value.trim()
                .replaceAll("y", "у")
                .replaceAll("f", "ф")
                .toLowerCase();
    }

}
