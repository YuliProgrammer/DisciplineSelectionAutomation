package com.discipline.selection.automation;

import com.discipline.selection.automation.dto.IncomingDataDto;
import com.discipline.selection.automation.exceptions.InvalidDataException;
import com.discipline.selection.automation.service.reader.ReaderChainImpl;
import com.discipline.selection.automation.service.writer.WriterChainImpl;
import com.discipline.selection.automation.util.Dialog;

import java.util.ArrayList;
import java.util.List;

public class MainApplication {

    public static String FILE_NAME;
    public static final List<String> SCHEDULE_FILE_NAMES = new ArrayList<>();

    public static void main(String... args) {
        try {
            FILE_NAME = Dialog.dialog(SCHEDULE_FILE_NAMES);
        } catch (InvalidDataException e) {
            return;
        }

        System.out.println("\nЧитання даних з вказаних файлiв...");

        ReaderChainImpl readerChain = new ReaderChainImpl();
        IncomingDataDto incomingDataDto = readerChain.read();

        WriterChainImpl writerChain = new WriterChainImpl(incomingDataDto);
        writerChain.write();

        System.out.println("\nКiнець роботи програми.");
    }
    // D:\University\Cursah\Test\2022 vxid bak 2sem22-2312.12.xlsx
    // D:\University\Cursah\Test\2022 rozklad bak. 2sem12.12.xlsx

}
