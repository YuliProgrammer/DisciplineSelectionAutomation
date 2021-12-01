package com.discipline.selection.automation.model.enums;

import com.discipline.selection.automation.exceptions.InvalidDataException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discipline.selection.automation.util.Constants.COMA;

public enum WeekDay {

    MONDAY(Arrays.asList("пн.", "пн")),
    TUESDAY(Arrays.asList("вт.", "вт")),
    WEDNESDAY(Arrays.asList("ср.", "ср")),
    THURSDAY(Arrays.asList("чт.", "чт")),
    FRIDAY(Arrays.asList("пт.", "пт")),
    SATURDAY(Arrays.asList("сб.", "сб"));

    private final List<String> names;
    public static final WeekDay[] DAYS =
            {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

    WeekDay(List<String> names) {
        this.names = names;
    }

    public static WeekDay of(String value, Integer rowIndex, String fileName) {
        return Stream.of(DAYS)
                .filter(weekDay -> weekDay.getNames().contains(value.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException(
                        String.format(
                                "Некоректний день тижня \"%s\" у файлi \"%s\" (рядок %d). Можливi форомати: %s.",
                                value, fileName, rowIndex + 1, Arrays.stream(DAYS)
                                        .map(WeekDay::getNames)
                                        .collect(Collectors.joining(COMA)))));
    }

    public String getName() {
        return this.names.get(0);
    }

    private String getNames() {
        return String.join(COMA, this.names);
    }

}
