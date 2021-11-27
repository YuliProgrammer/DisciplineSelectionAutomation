package com.discipline.selection.automation.mapper;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.LessonType;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discipline.selection.automation.util.Constants.COMA;

@UtilityClass
public class ScheduleMapper {

    /**
     * @param rowData  - map where key - it is a column index and value - it is column data
     * @param rowIndex - index of current row
     * @param fileName - name  of input file that contains this row
     * @return schedule
     */
    public List<Schedule> mapRowDataToSchedule(Map<Integer, String> rowData, Integer rowIndex, String fileName) {
        List<Schedule> schedules = getSchedule(rowData, rowIndex, fileName);
        List<Schedule> schedulesWithSeparateLessonTypes = new ArrayList<>();
        for (Schedule schedule : schedules) {
            schedulesWithSeparateLessonTypes.addAll(getSchedules(schedule, rowIndex));
        }

        return schedulesWithSeparateLessonTypes;
    }

    /**
     * Function map current row to desired object.
     *
     * @param rowData  -       map where key - it is a column index and value - it is column data
     * @param rowIndex - index of current row
     * @param fileName - name  of input file that contains this row
     * @return schedule
     */
    private List<Schedule> getSchedule(Map<Integer, String> rowData, Integer rowIndex, String fileName) {
        Schedule schedule = new Schedule();
        List<Schedule> schedules = new ArrayList<>();
        List<String> lessonNumbers = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
            String value = entry.getValue().trim();
            switch (entry.getKey()) {
                case 0:
                    schedule.setDisciplineCipher(value);
                    break;
                case 1:
                    schedule.setGroupCodes(Stream.of(value.split(COMA))
                            .map(String::trim)
                            .map(String::toUpperCase)
                            .collect(Collectors.toList()));
                    break;
                case 2:
                    schedule.setMaxNumberOfStudentsInSubGroup(StringMapper.parseStringToInt(value));
                    break;
                case 3:
                    schedule.setSubgroupNumber(value);
                    break;
                case 4:
                    schedule.setTeacherName(value);
                    break;
                case 5:
                    schedule.setTypeOfWeek(WeekType.of(value));
                    break;
                case 6:
                    lessonNumbers = Stream.of(value.split(COMA))
                            .map(String::trim)
                            .map(String::toUpperCase)
                            .collect(Collectors.toList());
                    break;
                case 7:
                    schedule.setDayOfWeek(value);
                    break;
                case 8:
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    schedule.setLessonType(LessonType.of(value, rowIndex, fileName));
                    break;
                case 9:
                    schedule.setGroupNumber(value);
                    break;
                case 10:
                    schedule.setFacultyType(FacultyType.of(StringMapper.parseStringToInt(value), rowIndex, fileName));
                    break;
                case 11:
                    schedule.setFacultyAddress(value);
                    break;
            }
        }

        for (String lessonNumber : lessonNumbers) {
            Schedule newSchedule = new Schedule(schedule);
            newSchedule.setLessonNumber(StringMapper.parseStringToInt(lessonNumber));
            schedules.add(newSchedule);
        }

        return schedules;
    }

    /**
     * Function that map current schedule to list.
     * If the lesson type of the current schedule is lecture+practice or lecture+laboratory than
     * there is two separate  schedules for lecture and practice or laboratory.
     *
     * @param schedule - current schedule
     * @param rowIndex - index of current row
     * @return list of schedules that was derived from the current schedule
     */
    private List<Schedule> getSchedules(Schedule schedule, Integer rowIndex) {
        List<Schedule> schedules = new ArrayList<>();
        LessonType lessonType = schedule.getLessonType();
        schedules.add(schedule);

        if (lessonType.equals(LessonType.LECTURE_AND_PRACTICE) ||
                lessonType.equals(LessonType.LECTURE_AND_LABORATORY)) {

            checkData(schedule, rowIndex);

            Schedule lectureSchedule = new Schedule(schedule);
            lectureSchedule.setTypeOfWeek(WeekType.NUMERATOR);   // числ
            lectureSchedule.setLessonType(LessonType.LECTURE);

            Schedule practiceSchedule = new Schedule(schedule);
            practiceSchedule.setTypeOfWeek(WeekType.DENOMINATOR); // знам
            practiceSchedule.setLessonType(lessonType.equals(LessonType.LECTURE_AND_PRACTICE) ? LessonType.PRACTICE :
                    LessonType.LABORATORY);
        }

        return schedules;
    }

    /**
     * Void validate current schedule for double lesson types.
     * For double lesson types, week type should be every week.
     *
     * @param schedule - current schedule for double lesson type
     * @param rowIndex - index of current row
     */
    private void checkData(Schedule schedule, Integer rowIndex) {
        LessonType lessonType = schedule.getLessonType();
        if (!schedule.getTypeOfWeek().equals(WeekType.EVERY_WEEK)) {
            throw new InvalidDataException(
                    String.format(
                            "Некоректнi даннi у рядку %d. Для вказаного типу заняття (\"%s\") колонка \"числ\\знам\" має бути порожньою.",
                            rowIndex, lessonType.getName()));
        }
    }

}
