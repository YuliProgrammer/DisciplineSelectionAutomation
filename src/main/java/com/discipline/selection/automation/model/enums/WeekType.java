package com.discipline.selection.automation.model.enums;

import java.util.stream.Stream;

public enum WeekType {

    NUMERATOR("числ."),
    DENOMINATOR("знам."),
    EVERY_WEEK("");

    private final String name;
    private static final WeekType[] WEEK_TYPES =
            {NUMERATOR, DENOMINATOR, EVERY_WEEK};

    WeekType(String name) {
        this.name = name;
    }

    public static WeekType of(String value) {
        return Stream.of(WEEK_TYPES)
                .filter(lessonType -> lessonType.getName().equals(value))
                .findFirst()
                .orElse(EVERY_WEEK);
    }

    public String getName() {
        return this.name;
    }

}
