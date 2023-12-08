package com.discipline.selection.automation.mapper;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Group;
import com.discipline.selection.automation.model.entity.GroupSchedule;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.ScheduleDate;
import com.discipline.selection.automation.model.entity.Teacher;
import com.discipline.selection.automation.model.entity.TeacherSchedule;
import com.discipline.selection.automation.model.enums.FacultyType;
import com.discipline.selection.automation.model.enums.LessonType;
import com.discipline.selection.automation.model.enums.WeekDay;
import com.discipline.selection.automation.model.enums.WeekType;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.discipline.selection.automation.util.Constants.COMA;
import static com.discipline.selection.automation.util.DisciplineCipherEngToUa.replaceEngByUa;

/**
 * Class creates Schedule object from Excel rows data
 *
 * @author Yuliia_Dolnikova
 */
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
        ScheduleDate scheduleDate = new ScheduleDate();

        List<Schedule> schedules = new ArrayList<>();
        List<String> lessonNumbers = new ArrayList<>();

        for (Map.Entry<Integer, String> entry : rowData.entrySet()) {
            String value = Objects.isNull(entry.getValue()) ? "" : entry.getValue().trim();
            schedule.setFileName(fileName);

            switch (entry.getKey()) {
                case 0:
                    schedule.setDiscipline(Discipline.builder().disciplineCipher(replaceEngByUa(value)).build());
                    break;
                case 1:
                    schedule.setGroupSchedule(Stream.of(value.split(COMA))
                            .map(String::trim).map(String::toUpperCase)
                            .map(groupCode -> GroupSchedule.builder()
                                    .schedule(schedule)
                                    .group(Group.builder().groupCode(groupCode).build())
                                    .build())
                            .collect(Collectors.toSet()));
                    break;
                case 2:
                    schedule.setMaxNumberOfStudentsInSubGroup(StringMapper.parseStringToInt(value));
                    break;
                case 3:
                    schedule.setSubgroupNumber(StringMapper.parseStringToInt(value));
                    break;
                case 4:
                    schedule.setTeacherSchedules(Stream.of(value.split(COMA))
                            .map(String::trim)
                            .map(teacherName -> TeacherSchedule.builder()
                                    .schedule(schedule)
                                    .teacher(Teacher.builder().name(teacherName).build())
                                    .build())
                            .collect(Collectors.toSet()));
                    break;
                case 5:
                    scheduleDate.setTypeOfWeek(WeekType.of(value));
                    break;
                case 6:
                    lessonNumbers = Stream.of(value.split(COMA))
                            .map(String::trim)
                            .map(String::toUpperCase)
                            .collect(Collectors.toList());
                    break;
                case 7:
                    scheduleDate.setDayOfWeek(WeekDay.of(value, rowIndex, fileName));
                    break;
                case 8:
                    if (value.endsWith(".")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    schedule.setLessonType(LessonType.of(value, rowIndex, fileName));
                    break;
                case 9:
                    schedule.setGroupNumber(StringMapper.parseStringToInt(value));
                    break;
                case 10:
                    schedule.setFacultyType(FacultyType.of(StringMapper.parseStringToInt(value), rowIndex, fileName));
                    break;
                case 11:
                    schedule.setFacultyAddress(value);
                    break;
            }
        }

        schedule.setScheduleDate(scheduleDate);
        for (String lessonNumber : lessonNumbers) {
            Schedule newSchedule = new Schedule(schedule);

            ScheduleDate newScheduleDate = newSchedule.getScheduleDate();
            newScheduleDate.setLessonNumber(StringMapper.parseStringToInt(lessonNumber));
            newSchedule.setScheduleDate(newScheduleDate);
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
            lectureSchedule.getScheduleDate().setTypeOfWeek(WeekType.NUMERATOR); // числ
            lectureSchedule.setLessonType(LessonType.LECTURE);

            Schedule practiceSchedule = new Schedule(schedule);
            practiceSchedule.getScheduleDate().setTypeOfWeek(WeekType.DENOMINATOR); // знам
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
        if (!schedule.getScheduleDate().getTypeOfWeek().equals(WeekType.EVERY_WEEK)) {
            throw new InvalidDataException(
                    String.format(
                            "Некоректнi даннi у рядку %d. Для вказаного типу заняття (\"%s\") колонка \"числ\\знам\" має бути порожньою.",
                            rowIndex, lessonType.getName()));
        }
    }

}
