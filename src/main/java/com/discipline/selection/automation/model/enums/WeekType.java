package com.discipline.selection.automation.model.enums;

import java.util.stream.Stream;


/**
 * Enum for all possible week types
 *
 * @author Yuliia_Dolnikova
 */
public enum WeekType {

    NUMERATOR("числ."),
    DENOMINATOR("знам."),
    EVERY_WEEK("");

    private final String name;
    private static final WeekType[] ALL_WEEK_TYPES = {NUMERATOR, DENOMINATOR, EVERY_WEEK};
    public static final WeekType[] WEEK_TYPES = {NUMERATOR, DENOMINATOR};

    WeekType(String name) {
        this.name = name;
    }

    public static WeekType of(String value) {
        return Stream.of(ALL_WEEK_TYPES)
                .filter(weekType -> weekType.getName().equals(value))
                .findFirst()
                .orElse(EVERY_WEEK);
    }

    public String getName() {
        return this.name;
    }

}
