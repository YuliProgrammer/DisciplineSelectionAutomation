package com.discipline.selection.automation.service.writer.common;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Interface that can write data to Excel workbook
 *
 * @author Yuliia_Dolnikova
 */
public interface Writer {

    void writeToExcel();

    /**
     * The void that save changes to workbook.
     *
     * @param file     - existed .xlsx file
     * @param workbook - current workbook
     */
    default void writeToWorkbook(File file, XSSFWorkbook workbook) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        workbook.write(outputStream);
        outputStream.close();
    }

}
