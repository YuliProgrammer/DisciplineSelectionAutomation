package com.discipline.selection.automation.service.reader.impl;

import com.discipline.selection.automation.mapper.DisciplineMapper;
import com.discipline.selection.automation.model.entity.Discipline;
import com.discipline.selection.automation.service.dao.DisciplineService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.discipline.selection.automation.MainApplication.FILE_NAME;
import static com.discipline.selection.automation.util.Constants.DISCIPLINES_SHEET_INDEX;

@Service
@AllArgsConstructor
public class ReadDisciplinesFromExcelImpl extends BasicExcelReaderChain<String, Discipline> {

    private final DisciplineService disciplineService;

    @Override
    public Map<String, Discipline> uploadData() {
        try (FileInputStream file = new FileInputStream(FILE_NAME)) {
            Workbook workbook = new XSSFWorkbook(file);

            Map<String, Discipline> disciplinesByCiphers = getDisciplines(workbook);
            System.out.println("\nДисципліни було успішно зчитано з файлу");

            disciplineService.saveDisciplines(disciplinesByCiphers);
            System.out.println("Дисципліни було успішно збережено в базі даних");
            incomingDataDto.setDisciplines(disciplinesByCiphers);
            return disciplinesByCiphers;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param workbook - input .xlsx workbook
     * @return map where key - it is a unique discipline cipher,
     * and value - it is a discipline that match to this discipline cipher
     */
    private Map<String, Discipline> getDisciplines(Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(DISCIPLINES_SHEET_INDEX);
        Map<String, Discipline> disciplines = new LinkedHashMap<>();

        for (Row row : sheet) {
            // row with index 0 - it is a table header
            if (row.getRowNum() == 0) {
                continue;
            }

            Map<Integer, String> rowData = addCellValuesToMap(row);
            if (rowData.get(0) == null || rowData.get(0).isEmpty()) {
                continue;
            }

            Discipline discipline = DisciplineMapper.mapRowDataToDiscipline(rowData);
            disciplines.put(discipline.getDisciplineCipher(), discipline);
        }

        return disciplines;
    }

}
