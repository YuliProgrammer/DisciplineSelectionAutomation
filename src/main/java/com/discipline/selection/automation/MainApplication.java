package com.discipline.selection.automation;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import com.discipline.selection.automation.service.reader.impl.ReadDisciplinesFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadScheduleFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadStudentsFromExcelImpl;
import com.discipline.selection.automation.service.writer.WriterChainImpl;
import com.discipline.selection.automation.util.Dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.GROUP;
import static com.discipline.selection.automation.util.Constants.TEACHER;

public class MainApplication {

    private static final ReadFromExcel<String, Map<String, List<Student>>> readStudentsFromExcel =
            new ReadStudentsFromExcelImpl();
    private static final ReadFromExcel<String, Discipline> readDisciplinesFromExcel =
            new ReadDisciplinesFromExcelImpl();
    private static final ReadFromExcel<String, Map<String, List<Schedule>>> readScheduleFromExcel =
            new ReadScheduleFromExcelImpl();

    public static String FILE_NAME;
    public static final List<String> SCHEDULE_FILE_NAMES = new ArrayList<>();

    public static void main(String... args) {
        try {
            FILE_NAME = Dialog.dialog(SCHEDULE_FILE_NAMES);
        } catch (InvalidDataException e) {
            return;
        }

        System.out.println("\nЧитання даних з вказаних файлiв...");

        Map<String, Map<String, List<Schedule>>> groupedSchedule = readScheduleFromExcel.uploadData();
        Map<String, List<Schedule>> schedulesGroupedByDisciplineCipher = groupedSchedule.get(DISCIPLINE);
        Map<String, List<Schedule>> schedulesGroupedByTeacher = groupedSchedule.get(TEACHER);

        Map<String, Map<String, List<Student>>> groupedStudents = readStudentsFromExcel.uploadData();
        Map<String, List<Student>> studentsGroupedByDiscipline = groupedStudents.get(DISCIPLINE);
        Map<String, List<Student>> studentsGroupedByGroup = groupedStudents.get(GROUP);

        Map<String, Discipline> disciplines = readDisciplinesFromExcel.uploadData();

        WriterChainImpl writerChain = new WriterChainImpl(studentsGroupedByGroup, studentsGroupedByDiscipline,
                disciplines, schedulesGroupedByDisciplineCipher, schedulesGroupedByTeacher);
        writerChain.write();

        System.out.println("\nКiнець роботи програми.");
    }

}
