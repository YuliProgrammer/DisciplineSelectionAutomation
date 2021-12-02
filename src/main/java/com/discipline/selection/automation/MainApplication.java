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

import static com.discipline.selection.automation.util.Constants.DISCIPLINE;
import static com.discipline.selection.automation.util.Constants.GROUP;

public class MainApplication {

    private final static ReadFromExcel<String, Map<String, List<Student>>> readStudentsFromExcel =
            new ReadStudentsFromExcelImpl();
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

        Map<String, Map<String, List<Student>>> groupedStudents = readStudentsFromExcel.uploadData();
        Map<String, List<Student>> studentsGroupedByDiscipline = groupedStudents.get(DISCIPLINE);
        Map<String, List<Student>> studentsGroupedByGroup = groupedStudents.get(GROUP);

        Map<String, Discipline> disciplines = readDisciplinesFromExcel.uploadData();

        System.out.println("\nПiдрахунок та запис поточної кiлькостi студентiв...");
        Writer writeStudentsCount = new WriteStudentsCount(studentsGroupedByDiscipline, disciplines);
        writeStudentsCount.writeToExcel();

        Writer writeConsolidationOfDisciplines = new WriteConsolidationOfDisciplines(studentsGroupedByGroup,
                studentsGroupedByDiscipline, disciplines, schedule);
        writeConsolidationOfDisciplines.writeToExcel();

        System.out.println("\nКiнець роботи програми.");
    }

    // D:\University\Cursah\New\test\2021 vxid bak 10_2021.xlsx
    // D:\University\Cursah\New\test\2021_Rozklad bak 2sem.xlsx
}
