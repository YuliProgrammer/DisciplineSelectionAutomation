package com.discipline.selection.automation.service.reader;

import com.discipline.selection.automation.dto.IncomingDataDto;
import com.discipline.selection.automation.model.Discipline;
import com.discipline.selection.automation.model.Schedule;
import com.discipline.selection.automation.model.Student;
import com.discipline.selection.automation.service.reader.impl.ReadDisciplinesFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadDisciplinesHeaderFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadScheduleFromExcelImpl;
import com.discipline.selection.automation.service.reader.impl.ReadStudentsFromExcelImpl;

import java.util.List;
import java.util.Map;

public class ReaderChainImpl {

    public IncomingDataDto read() {
        ReadFromExcel<String, Map<String, List<Student>>> readStudentsFromExcel =
                new ReadStudentsFromExcelImpl();

        ReadFromExcel<String, Discipline> readDisciplinesFromExcel =
                new ReadDisciplinesFromExcelImpl();

        ReadFromExcel<Integer, String> readDisciplinesHeaderFromExcel =
                new ReadDisciplinesHeaderFromExcelImpl();

        ReadFromExcel<String, Map<String, List<Schedule>>> readScheduleFromExcel =
                new ReadScheduleFromExcelImpl();

        readStudentsFromExcel.setNextReader(readDisciplinesFromExcel);
        readDisciplinesFromExcel.setNextReader(readDisciplinesHeaderFromExcel);
        readDisciplinesHeaderFromExcel.setNextReader(readScheduleFromExcel);

        readStudentsFromExcel.execute();

        return readScheduleFromExcel.getIncomingDataDto();
    }

}
