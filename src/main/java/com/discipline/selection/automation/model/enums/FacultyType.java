package com.discipline.selection.automation.model.enums;

import com.discipline.selection.automation.exceptions.InvalidDataException;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discipline.selection.automation.util.Constants.COMA;

/**
 * Enum for all possible faculties types - city's regions where faculty is located
 *
 * @author Yuliia_Dolnikova
 */
public enum FacultyType {

    ONLINE(0),
    GAGARINA(1), // Корпуса на проспекті Гагаріна
    CENTER(2),   // Корпуса 2, 3, 4, 5 на Яворницькокого
    OTHER(3);

    private final int type;
    private static final FacultyType[] FACULTY_TYPES = {ONLINE, GAGARINA, CENTER, OTHER};

    FacultyType(int type) {
        this.type = type;
    }

    public static FacultyType of(int value, Integer rowIndex, String fileName) {
        return Stream.of(FACULTY_TYPES)
                .filter(facultyType -> facultyType.getType() == value)
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(
                        String.format(
                                "Некоректний тип місця проведення \"%s\" у файлi \"%s\" (рядок %d). Можливi форомати: %s.",
                                value, fileName, rowIndex + 1,
                                Arrays.stream(FACULTY_TYPES)
                                        .map(FacultyType::toString)
                                        .collect(Collectors.joining(COMA)))));
    }

    public int getType() {
        return this.type;
    }

    public String toString() {
        return Integer.toString(this.type);
    }
}
