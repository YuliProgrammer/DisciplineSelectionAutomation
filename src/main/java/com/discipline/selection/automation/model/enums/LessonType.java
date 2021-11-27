package com.discipline.selection.automation.model.enums;

import com.discipline.selection.automation.exceptions.InvalidDataException;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discipline.selection.automation.util.Constants.COMA;

public enum LessonType {

    LECTURE("лк"),
    LABORATORY("лаб"),
    PRACTICE("пр"),
    LECTURE_AND_PRACTICE("лк\\пр"),
    LECTURE_AND_LABORATORY("лк\\лаб");

    private final String name;
    private static final LessonType[] LESSON_TYPES =
            {LECTURE, LABORATORY, PRACTICE,
                    LECTURE_AND_PRACTICE, LECTURE_AND_LABORATORY};

    LessonType(String name) {
        this.name = name;
    }

    public static LessonType of(String value, Integer rowIndex, String fileName) {
        return Stream.of(LESSON_TYPES)
                .filter(lessonType -> lessonType.getName().equals(value))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(
                        String.format("Некоректний тип заняття \"%s\" у файлi \"%s\" (рядок %d). Можливi форомати: %s.",
                                value, fileName, rowIndex,
                                Arrays.stream(LESSON_TYPES).map(LessonType::getName)
                                        .collect(Collectors.joining(COMA)))));
    }

    public String getName() {
        return this.name;
    }

}
