package com.discipline.selection.automation;

import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.reader.ReadFromExcel;
import com.discipline.selection.automation.service.reader.impl.ReadDisciplinesFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadScheduleFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadStudentsFromExcelImpl;
import com.discipline.selection.automation.service.writer.common.Writer;
import com.discipline.selection.automation.service.writer.common.impl.WriteConsolidationOfDisciplines;
import com.discipline.selection.automation.service.writer.common.impl.WriteStudentsCount;
import com.discipline.selection.automation.util.Dialog;

import java.util.List;
import java.util.Map;

public class MainApplication {

    private final static ReadFromExcel<String, List<Student>> readStudentsFromExcel = new ReadStudentsFromExcelImpl();
    private final static ReadFromExcel<String, Discipline> readDisciplinesFromExcel =
            new ReadDisciplinesFromExcelImpl();
    private final static ReadFromExcel<String, List<Schedule>> readScheduleFromExcel = new ReadScheduleFromExcelImpl();

    public static List<String> FILE_NAMES;

    public static void main(String... args) {
        try {
            FILE_NAMES = Dialog.dialog();
        } catch (InvalidDataException e) {
            return;
        }

        System.out.println("\nЧитання даних з вказаних файлiв...");

        Map<String, List<Schedule>> schedule = readScheduleFromExcel.uploadData();
        Map<String, List<Student>> students = readStudentsFromExcel.uploadData();
        Map<String, Discipline> disciplines = readDisciplinesFromExcel.uploadData();

        System.out.println("\nПiдрахунок та запис поточної кiлькостi студентiв...");
        Writer writeStudentsCount = new WriteStudentsCount(students, disciplines);
        writeStudentsCount.writeToExcel();

        System.out.println("\nЗапис зведення дисциплiн...");
        Writer writeConsolidationOfDisciplines = new WriteConsolidationOfDisciplines(students, disciplines, schedule);
        writeConsolidationOfDisciplines.writeToExcel();

        System.out.println("\nКiнець роботи програми.");
    }

}
