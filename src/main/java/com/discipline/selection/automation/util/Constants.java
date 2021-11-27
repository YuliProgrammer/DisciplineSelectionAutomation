package com.discipline.selection.automation.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

@UtilityClass
public class Constants {

    public static final Integer STUDENTS_SHEET_INDEX = 0;
    public static final Integer SCHEDULE_SHEET_INDEX = 0;
    public static final Integer DISCIPLINES_SHEET_INDEX = 1;

    public static final String COMA = ",";
    public static final String SEMICOLON = ";\n";
    public static final String BLANK_LINE = " ";
    public static final String XLS_FILE_FORMAT = ".xls";
    public static final String XLSX_FILE_FORMAT = ".xlsx";

    public static final Integer ONE = 1;
    public static final Integer NUMBER_OF_STUDENTS_IN_FLOW = 75;
    public static final Integer NUMBER_OF_STUDENTS_IN_GROUP = 30;

    public static final String OUTPUT_FILE_NAME = "_Розклад_обраних_вибiркових_дисциплiн.xlsx";
    public static final String STUDENTS_COUNT_COLUMN_TITLE = "К-ть студентiв";
    public static final String CONSOLIDATION_OF_DISCIPLINES_SHEET_NAME = "Зведення дисц шифр спец";
    public static final String CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_SHEET_NAME = "Звед студ дисципл розклад ";
    public static final String CONSOLIDATION_OF_DISCIPLINES_DUPLICATED_SCHEDULE_SHEET_NAME =
            "Звед студ дисц розклад (дубл.)";

    public static final String CHOSEN_DISCIPLINES_FOR_DIFFERENT_FACILITIES_SHEET_NAME =
            "Назви обраних рiзними фак дисц";

    public static final Set<String> CONSOLIDATION_OF_DISCIPLINES_HEADER = new LinkedHashSet<>(Arrays.asList(
            "Назва дисциплiни + кафедра (факультет)", "Шифр",
            "К-ть годин лекцiй на тиждень", "К-ть годин практик. (сем.) на тиждень", "К-ть годин лабор. на тиждень",
            "Н", "К-ть груп", "К-ть пiдгруп", "НПП", "Усього"));

    public static final Set<String> CONSOLIDATION_OF_DISCIPLINES_SCHEDULE_HEADER = new LinkedHashSet<>(Arrays.asList(
            "1 буква фак. - це 1 буква коду групи", "Шифр дисциплiни", "Назва дисциплiни",
            "iм'я та прiзвище студента", "Шифр групи",
            "Розклад занять (Прiзище та iм'я НПП, днi тижня, пара, види занять)",
            "Місце проведення", "Корпус/аудиторія, де будуть проводиться заняття"));

}
