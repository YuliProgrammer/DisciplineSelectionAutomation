package com.discipline.selection.automation.service.dto;

import com.discipline.selection.automation.util.CellStyleCreator;
import lombok.Data;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Data
public class CellStyles {

    private XSSFCellStyle emptyCellStyle;
    private XSSFCellStyle evenCellStyle;
    private XSSFCellStyle oddCellStyle;
    private XSSFCellStyle farCellStyle;
    private XSSFCellStyle duplicatedCellStyle;
    private XSSFCellStyle duplicatedAndFarCellStyle;

    private static CellStyles instance;

    public CellStyles(XSSFWorkbook workbook) {
        emptyCellStyle = CellStyleCreator.createDefaultCellStyleCharacteristics(workbook);
        evenCellStyle = CellStyleCreator.createEvenCellStyleCharacteristics(workbook);
        oddCellStyle = CellStyleCreator.createOddCellStyleCharacteristics(workbook);
        farCellStyle = CellStyleCreator.createFarCellStyleCharacteristics(workbook);
        duplicatedCellStyle = CellStyleCreator.createDuplicatedCellStyleCharacteristics(workbook);
        duplicatedAndFarCellStyle = CellStyleCreator.createDuplicatedFarCellStyleCharacteristics(workbook);
    }

    public static synchronized CellStyles getInstance(XSSFWorkbook workbook) {
        if (instance == null) {
            instance = new CellStyles(workbook);
        }
        return instance;
    }

}
