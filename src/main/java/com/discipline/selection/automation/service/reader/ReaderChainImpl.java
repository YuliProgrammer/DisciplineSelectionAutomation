package com.discipline.selection.automation.service.reader;

import com.discipline.selection.automation.dto.IncomingDataDto;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.model.entity.Schedule;
import com.discipline.selection.automation.model.entity.Student;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ReaderChainImpl {

    private final ReadFromExcel<String, Discipline> readDisciplinesFromExcelImpl;
    private final ReadFromExcel<Integer, String> readDisciplinesHeaderFromExcelImpl;
    private final ReadFromExcel<String, Map<String, List<Student>>> readStudentsFromExcelImpl;
    private final ReadFromExcel<String, Map<String, List<Schedule>>> readScheduleFromExcelImpl;

    public IncomingDataDto read() {
        readDisciplinesFromExcelImpl.setNextReader(readDisciplinesHeaderFromExcelImpl);
        readDisciplinesHeaderFromExcelImpl.setNextReader(readStudentsFromExcelImpl);
        readStudentsFromExcelImpl.setNextReader(readScheduleFromExcelImpl);

        readDisciplinesFromExcelImpl.execute();

        return readScheduleFromExcelImpl.getIncomingDataDto();
    }

}
