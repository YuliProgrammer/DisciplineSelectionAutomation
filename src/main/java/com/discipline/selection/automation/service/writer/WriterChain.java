package com.discipline.selection.automation.service.writer;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public interface WriterChain {

    /**
     * @param nextWriter - next writer in the chain
     */
    void setNextWriter(WriterChain nextWriter);

    /**
     * The method execute the chain of writers
     */
    void execute();

    /**
     * The method checks whether the writer should write smth in the workbook or not
     *
     * @return true, when we writeToExcel method should be called
     */
    boolean isProcess();

    /**
     * The method returns the file name for the workbook
     *
     * @return the file name of the current workbook for writing data in the existed workbook
     * or new generated file name for all new workbooks
     */
    String getFileName();

    /**
     * The method writes data to the workbook
     *
     * @param fileName - name of the workbook
     */
    void writeToExcel(String fileName);

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
